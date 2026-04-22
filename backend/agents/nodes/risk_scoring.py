"""
Risk Scoring Agent Node
Calculates overall risk score (0-10 CVSS-style) based on:
  - Finding severity distribution
  - Exploitability (exported components, debuggable, cleartext)
  - Business impact
Populates: risk_score
"""
from loguru import logger
from backend.agents.state import AgentState
from backend.models.schema import RiskScore, Severity, AppMetadata, Vulnerability
from typing import List


SEVERITY_WEIGHTS = {
    "Critical": 10.0,
    "High": 7.5,
    "Medium": 5.0,
    "Low": 2.5,
    "Info": 0.5,
}

SEVERITY_THRESHOLDS = {
    "Critical": (8.5, 10.0),
    "High": (6.5, 8.4),
    "Medium": (4.0, 6.4),
    "Low": (0.0, 3.9),
}


def _score_to_severity(score: float) -> Severity:
    for sev, (low, high) in SEVERITY_THRESHOLDS.items():
        if low <= score <= high:
            return Severity(sev)
    return Severity.LOW


async def risk_scoring_node(state: AgentState) -> AgentState:
    logger.info(f"[RiskScoring] Starting | scan_id={state.get('scan_id')}")
    state["current_step"] = "risk_scoring"

    findings: List[Vulnerability] = state.get("enriched_findings", state.get("static_findings", []))
    metadata: AppMetadata = state.get("metadata") or AppMetadata(app_name="unknown")

    if not findings:
        state["risk_score"] = RiskScore(
            overall=Severity.INFO,
            score=0.0,
            exploitability=0.0,
            impact=0.0,
            business_risk=0.0,
            rationale="No vulnerabilities detected.",
        )
        return state

    # ── Exploitability Score ────────────────────────────────────────
    exploitability = 0.0
    exploitability_reasons = []

    if metadata.debuggable:
        exploitability += 2.0
        exploitability_reasons.append("app is debuggable")
    if metadata.uses_cleartext_traffic:
        exploitability += 1.5
        exploitability_reasons.append("cleartext traffic allowed")
    if metadata.exported_activities:
        exploitability += min(len(metadata.exported_activities) * 0.5, 2.0)
        exploitability_reasons.append(f"{len(metadata.exported_activities)} exported activities")
    if metadata.exported_services:
        exploitability += min(len(metadata.exported_services) * 0.5, 1.5)
    if any(v.title == "SSL/TLS Certificate Validation Disabled" for v in findings):
        exploitability += 2.5
        exploitability_reasons.append("SSL validation disabled")

    exploitability = min(exploitability, 10.0)

    # ── Impact Score ────────────────────────────────────────────────
    severity_counts = {"Critical": 0, "High": 0, "Medium": 0, "Low": 0, "Info": 0}
    for v in findings:
        sev = v.severity if isinstance(v.severity, str) else v.severity.value
        severity_counts[sev] = severity_counts.get(sev, 0) + 1

    impact = (
        severity_counts["Critical"] * 10.0
        + severity_counts["High"] * 7.0
        + severity_counts["Medium"] * 4.0
        + severity_counts["Low"] * 1.5
    ) / max(len(findings), 1)
    impact = min(impact, 10.0)

    # ── Business Risk ───────────────────────────────────────────────
    # Dangerous permissions elevate business risk
    dangerous_perm_count = sum(1 for p in metadata.permissions if p.is_dangerous)
    business_risk = min(
        3.0
        + (severity_counts["Critical"] * 2.0)
        + (dangerous_perm_count * 0.3),
        10.0,
    )

    # ── Overall Score (weighted average) ───────────────────────────
    overall_score = round((exploitability * 0.35 + impact * 0.45 + business_risk * 0.20), 2)
    overall_severity = _score_to_severity(overall_score)

    # ── Rationale ───────────────────────────────────────────────────
    rationale_parts = [
        f"Detected {len(findings)} total findings: "
        f"{severity_counts['Critical']} Critical, {severity_counts['High']} High, "
        f"{severity_counts['Medium']} Medium, {severity_counts['Low']} Low.",
    ]
    if exploitability_reasons:
        rationale_parts.append(f"Exploitability factors: {', '.join(exploitability_reasons)}.")
    if dangerous_perm_count:
        rationale_parts.append(f"App requests {dangerous_perm_count} dangerous permissions.")

    state["risk_score"] = RiskScore(
        overall=overall_severity,
        score=overall_score,
        exploitability=round(exploitability, 2),
        impact=round(impact, 2),
        business_risk=round(business_risk, 2),
        rationale=" ".join(rationale_parts),
    )

    logger.info(
        f"[RiskScoring] Score={overall_score}/10 | Severity={overall_severity}"
    )
    return state
