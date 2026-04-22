"""
Threat Modeling Agent Node
Uses LLM to identify attack vectors, threat scenarios, and data exposure risks.
Populates: threat_model
"""
import json
from loguru import logger
from langchain_core.prompts import ChatPromptTemplate

from backend.agents.state import AgentState
from backend.llm.client import get_llm_client
from backend.models.schema import ThreatModel, ThreatScenario, Severity, AppMetadata


THREAT_MODEL_PROMPT = ChatPromptTemplate.from_messages([
    ("system", """You are a senior threat modeling expert specializing in Android security.
Analyze the application metadata and vulnerabilities, then produce a structured threat model.

Return ONLY valid JSON in this exact format:
{{
  "attack_surface": ["surface1", "surface2", ...],
  "scenarios": [
    {{
      "threat_actor": "External attacker | Malicious app | Insider",
      "attack_vector": "Network | Local | Physical",
      "scenario": "Detailed description of the attack scenario",
      "impact": "What data or functionality is at risk",
      "likelihood": "Critical|High|Medium|Low"
    }}
  ],
  "data_exposure_risks": ["risk1", "risk2", ...]
}}
Provide 3-5 scenarios. Focus on realistic, high-impact threats."""),
    ("human", """App Metadata:
Package: {package}
Permissions: {permissions}
Exported Components: {exported}
Debuggable: {debuggable}
Cleartext Traffic: {cleartext}

Top Vulnerabilities:
{vulnerabilities}

OWASP Context:
{owasp_context}"""),
])


async def threat_model_node(state: AgentState) -> AgentState:
    logger.info(f"[ThreatModel] Starting | scan_id={state.get('scan_id')}")
    state["current_step"] = "threat_modeling"
    state.setdefault("errors", [])

    metadata: AppMetadata = state.get("metadata") or AppMetadata(app_name="unknown")
    findings = state.get("enriched_findings", state.get("static_findings", []))
    owasp_ctx = state.get("owasp_context", "")

    # Format top 10 findings for the prompt
    top_vulns = "\n".join(
        f"- [{v.severity}] {v.title}: {v.description[:150]}"
        for v in sorted(findings, key=lambda x: {"Critical": 0, "High": 1, "Medium": 2, "Low": 3, "Info": 4}.get(x.severity, 5))[:10]
    )

    dangerous_perms = [p.name for p in metadata.permissions if p.is_dangerous]
    exported = (
        metadata.exported_activities
        + metadata.exported_services
        + metadata.exported_receivers
        + metadata.exported_providers
    )

    try:
        llm = get_llm_client().reasoning_model()
        chain = THREAT_MODEL_PROMPT | llm
        response = await chain.ainvoke({
            "package": metadata.package_name or "unknown",
            "permissions": ", ".join(dangerous_perms[:10]) or "None",
            "exported": ", ".join(exported[:10]) or "None",
            "debuggable": str(metadata.debuggable),
            "cleartext": str(metadata.uses_cleartext_traffic),
            "vulnerabilities": top_vulns or "No findings",
            "owasp_context": owasp_ctx[:1500],
        })

        data = json.loads(response.content)
        scenarios = [
            ThreatScenario(
                threat_actor=s.get("threat_actor", "Unknown"),
                attack_vector=s.get("attack_vector", "Unknown"),
                scenario=s.get("scenario", ""),
                impact=s.get("impact", ""),
                likelihood=Severity(s.get("likelihood", "Medium")),
            )
            for s in data.get("scenarios", [])
        ]

        state["threat_model"] = ThreatModel(
            attack_surface=data.get("attack_surface", []),
            scenarios=scenarios,
            data_exposure_risks=data.get("data_exposure_risks", []),
        )
        logger.info(f"[ThreatModel] Generated {len(scenarios)} threat scenarios")

    except Exception as e:
        logger.error(f"[ThreatModel] LLM failed: {e}")
        state["errors"].append(f"Threat modeling error: {e}")
        # Fallback: Generate basic threat model from metadata
        state["threat_model"] = _fallback_threat_model(metadata, findings)

    return state


def _fallback_threat_model(metadata: AppMetadata, findings: list) -> ThreatModel:
    """Basic rule-based threat model if LLM is unavailable."""
    attack_surface = []
    if metadata.exported_activities:
        attack_surface.append(f"{len(metadata.exported_activities)} exported activities")
    if metadata.exported_services:
        attack_surface.append(f"{len(metadata.exported_services)} exported services")
    if metadata.uses_cleartext_traffic:
        attack_surface.append("Cleartext HTTP traffic")
    if metadata.debuggable:
        attack_surface.append("Debug interface enabled")

    scenarios = []
    if metadata.uses_cleartext_traffic:
        scenarios.append(ThreatScenario(
            threat_actor="Network attacker",
            attack_vector="Network (MITM)",
            scenario="Attacker intercepts cleartext HTTP traffic to steal data or inject malicious responses.",
            impact="Data theft, session hijacking, credential exposure",
            likelihood=Severity.HIGH,
        ))
    if metadata.debuggable:
        scenarios.append(ThreatScenario(
            threat_actor="Local attacker",
            attack_vector="Local ADB access",
            scenario="Attacker uses ADB to attach debugger, dump memory, or extract runtime secrets.",
            impact="Full app compromise, credential extraction",
            likelihood=Severity.HIGH,
        ))

    return ThreatModel(
        attack_surface=attack_surface,
        scenarios=scenarios,
        data_exposure_risks=[v.title for v in findings if "storage" in v.title.lower()],
    )
