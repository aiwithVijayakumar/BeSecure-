"""
Report Generator Agent Node
Assembles all agent outputs into the final SecurityReport.
Populates: report
"""
from datetime import datetime
from loguru import logger

from backend.agents.state import AgentState
from backend.models.schema import (
    SecurityReport, VulnerabilitySummary, DeveloperChecklist,
    InputType, ScanStatus, Severity, OWASPCategory, RiskScore
)


def _count_severities(findings: list) -> VulnerabilitySummary:
    counts = {"Critical": 0, "High": 0, "Medium": 0, "Low": 0, "Info": 0}
    for v in findings:
        sev = v.severity if isinstance(v.severity, str) else v.severity.value
        counts[sev] = counts.get(sev, 0) + 1
    return VulnerabilitySummary(
        critical=counts["Critical"],
        high=counts["High"],
        medium=counts["Medium"],
        low=counts["Low"],
        info=counts["Info"],
        total=len(findings),
    )


def _build_owasp_compliance(findings: list) -> dict:
    """Map each OWASP category to pass/fail based on findings."""
    flagged = {
        v.owasp_category if isinstance(v.owasp_category, str) else v.owasp_category.value
        for v in findings
        if v.severity in ("Critical", "High", "Medium")
    }
    compliance = {}
    for cat in OWASPCategory:
        if cat == OWASPCategory.UNKNOWN:
            continue
        val = cat.value
        compliance[val] = "FAIL" if val in flagged else "PASS"
    return compliance


def _build_developer_checklist(findings: list) -> DeveloperChecklist:
    """Generate a de-duplicated checklist from all finding fixes."""
    seen = set()
    items = []
    for v in sorted(findings, key=lambda x: {"Critical": 0, "High": 1, "Medium": 2, "Low": 3, "Info": 4}.get(
        x.severity if isinstance(x.severity, str) else x.severity.value, 5
    )):
        fix = v.fix
        if fix not in seen:
            seen.add(fix)
            sev = v.severity if isinstance(v.severity, str) else v.severity.value
            items.append(f"[{sev}] {fix}")
    return DeveloperChecklist(items=items)


def _build_executive_summary(findings: list, risk_score: RiskScore, metadata) -> str:
    vuln_counts = _count_severities(findings)
    app_name = getattr(metadata, "package_name", None) or getattr(metadata, "app_name", "Unknown App")
    sev = risk_score.overall if isinstance(risk_score.overall, str) else risk_score.overall.value
    return (
        f"Security analysis of '{app_name}' identified {vuln_counts.total} vulnerabilities "
        f"({vuln_counts.critical} Critical, {vuln_counts.high} High, "
        f"{vuln_counts.medium} Medium, {vuln_counts.low} Low). "
        f"The overall risk rating is {sev} with a score of {risk_score.score}/10. "
        f"{risk_score.rationale}"
    )


async def report_gen_node(state: AgentState) -> AgentState:
    logger.info(f"[ReportGen] Starting | scan_id={state.get('scan_id')}")
    state["current_step"] = "report_generation"

    findings = state.get("remediated_findings", state.get("enriched_findings", state.get("static_findings", [])))
    metadata = state.get("metadata")
    risk_score = state.get("risk_score") or RiskScore(
        overall=Severity.INFO,
        score=0.0,
        exploitability=0.0,
        impact=0.0,
        business_risk=0.0,
        rationale="No score computed.",
    )

    app_name = getattr(metadata, "app_name", state.get("app_name", "Unknown App"))

    report = SecurityReport(
        scan_id=state["scan_id"],
        app_name=app_name,
        input_type=InputType(state.get("input_type", "apk")),
        status=ScanStatus.COMPLETED,
        created_at=datetime.utcnow(),
        completed_at=datetime.utcnow(),
        metadata=metadata,
        risk_score=risk_score,
        summary=_build_executive_summary(findings, risk_score, metadata),
        vulnerabilities=findings,
        vuln_summary=_count_severities(findings),
        threat_model=state.get("threat_model"),
        developer_checklist=_build_developer_checklist(findings),
        owasp_compliance=_build_owasp_compliance(findings),
    )

    state["report"] = report
    logger.info(
        f"[ReportGen] Report assembled | "
        f"vulns={len(findings)} | score={risk_score.score}"
    )
    return state
