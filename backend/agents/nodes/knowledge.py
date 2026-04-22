"""
Security Knowledge Agent Node (RAG)
Enriches findings with OWASP + SDLC context via ChromaDB retrieval.
Populates: owasp_context, sdlc_context, enriched_findings
"""
from loguru import logger
from langchain_core.documents import Document

from backend.agents.state import AgentState
from backend.rag.vectorstore import get_owasp_retriever, get_sdlc_retriever
from backend.models.schema import Vulnerability, OWASPCategory
from backend.llm.client import get_llm_client
from langchain_core.prompts import ChatPromptTemplate


ENRICHMENT_PROMPT = ChatPromptTemplate.from_messages([
    ("system", """You are a senior Android security expert.
Given a vulnerability finding and relevant OWASP / SDLC knowledge, 
enrich the finding with:
1. A detailed technical description
2. Real-world attack scenario
3. Business impact
4. The SDLC phase where this should be caught

Respond in this exact JSON format:
{{
  "description": "...",
  "attack_vector": "...",
  "impact": "...",
  "sdlc_phase": "design|coding|testing|deployment"
}}
Only return valid JSON, nothing else."""),
    ("human", """Finding: {finding}
OWASP Context: {owasp_context}
SDLC Context: {sdlc_context}"""),
])


async def knowledge_node(state: AgentState) -> AgentState:
    logger.info(f"[Knowledge] Starting | scan_id={state.get('scan_id')}")
    state["current_step"] = "knowledge"
    state.setdefault("errors", [])

    findings: list[Vulnerability] = state.get("static_findings", [])
    if not findings:
        state["owasp_context"] = ""
        state["sdlc_context"] = ""
        state["enriched_findings"] = []
        return state

    # ── Build aggregate query from all finding titles ───────────────
    query = " ".join(set(v.title for v in findings))

    owasp_context = ""
    sdlc_context = ""

    try:
        owasp_retriever = get_owasp_retriever(k=6)
        if owasp_retriever:
            owasp_docs: list[Document] = await owasp_retriever.ainvoke(query)
            owasp_context = "\n\n".join(d.page_content for d in owasp_docs)
            logger.info(f"[Knowledge] Retrieved {len(owasp_docs)} OWASP chunks")
    except Exception as e:
        logger.warning(f"[Knowledge] OWASP retrieval failed: {e}")

    try:
        sdlc_retriever = get_sdlc_retriever(k=4)
        if sdlc_retriever:
            sdlc_docs: list[Document] = await sdlc_retriever.ainvoke(query)
            sdlc_context = "\n\n".join(d.page_content for d in sdlc_docs)
            logger.info(f"[Knowledge] Retrieved {len(sdlc_docs)} SDLC chunks")
    except Exception as e:
        logger.warning(f"[Knowledge] SDLC retrieval failed: {e}")

    state["owasp_context"] = owasp_context
    state["sdlc_context"] = sdlc_context

    # ── Enrich top-severity findings with LLM ───────────────────────
    enriched = list(findings)
    critical_high = [f for f in findings if f.severity in ("Critical", "High")][:5]

    llm = get_llm_client().reasoning_model()

    for vuln in critical_high:
        try:
            chain = ENRICHMENT_PROMPT | llm
            response = await chain.ainvoke({
                "finding": f"{vuln.title}: {vuln.evidence[:300]}",
                "owasp_context": owasp_context[:2000],
                "sdlc_context": sdlc_context[:1000],
            })
            import json
            enrichment = json.loads(response.content)

            # Update the finding in the list
            idx = next(i for i, f in enumerate(enriched) if f.id == vuln.id)
            enriched[idx] = vuln.model_copy(update={
                "description": enrichment.get("description", vuln.description),
                "attack_vector": enrichment.get("attack_vector", vuln.attack_vector),
                "impact": enrichment.get("impact", vuln.impact),
                "sdlc_phase": enrichment.get("sdlc_phase", vuln.sdlc_phase),
            })
            logger.info(f"[Knowledge] Enriched: {vuln.title}")
        except Exception as e:
            logger.warning(f"[Knowledge] Enrichment failed for '{vuln.title}': {e}")

    state["enriched_findings"] = enriched
    return state
