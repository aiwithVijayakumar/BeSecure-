"""
Remediation Agent Node
LLM generates code-level fixes for critical/high findings.
Populates: remediated_findings
"""
import json
from loguru import logger
from langchain_core.prompts import ChatPromptTemplate

from backend.agents.state import AgentState
from backend.llm.client import get_llm_client
from backend.models.schema import Vulnerability, CodeFix


REMEDIATION_PROMPT = ChatPromptTemplate.from_messages([
    ("system", """You are an Android security expert who writes secure code fixes.
Given a vulnerability, provide a concrete code fix.

Return ONLY valid JSON:
{{
  "language": "java",
  "vulnerable_code": "// The vulnerable code snippet",
  "fixed_code": "// The secure replacement code",  
  "explanation": "Brief explanation of why this fix works"
}}
Be specific. Show real Android/Java/Kotlin code examples."""),
    ("human", """Vulnerability: {title}
Severity: {severity}
Evidence: {evidence}
Current Fix Suggestion: {current_fix}
OWASP Context: {context}"""),
])


async def remediation_node(state: AgentState) -> AgentState:
    logger.info(f"[Remediation] Starting | scan_id={state.get('scan_id')}")
    state["current_step"] = "remediation"
    state.setdefault("errors", [])

    findings: list[Vulnerability] = state.get("enriched_findings", state.get("static_findings", []))
    owasp_ctx = state.get("owasp_context", "")

    if not findings:
        state["remediated_findings"] = []
        return state

    llm = get_llm_client().code_model()
    remediated = list(findings)

    # Generate code fixes only for Critical and High (cost-effective)
    priority_findings = [
        (i, f) for i, f in enumerate(findings)
        if f.severity in ("Critical", "High") and f.code_fix is None
    ][:8]  # Cap at 8 LLM calls

    for idx, vuln in priority_findings:
        try:
            chain = REMEDIATION_PROMPT | llm
            response = await chain.ainvoke({
                "title": vuln.title,
                "severity": vuln.severity,
                "evidence": vuln.evidence[:300],
                "current_fix": vuln.fix,
                "context": owasp_ctx[:800],
            })

            fix_data = json.loads(response.content)
            code_fix = CodeFix(
                language=fix_data.get("language", "java"),
                vulnerable_code=fix_data.get("vulnerable_code"),
                fixed_code=fix_data.get("fixed_code", "// See description"),
                explanation=fix_data.get("explanation", ""),
            )
            remediated[idx] = vuln.model_copy(update={"code_fix": code_fix})
            logger.info(f"[Remediation] Generated code fix for: {vuln.title}")

        except Exception as e:
            logger.warning(f"[Remediation] Code fix failed for '{vuln.title}': {e}")

    state["remediated_findings"] = remediated
    return state
