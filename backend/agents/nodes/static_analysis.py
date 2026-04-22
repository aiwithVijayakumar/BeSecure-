"""
Static Analysis Agent Node
Runs rule-based SAST + optional MobSF scan.
Populates: static_findings, mobsf_findings
"""
from loguru import logger
from pathlib import Path

from backend.agents.state import AgentState
from backend.services.static_analyzer import StaticAnalyzer
from backend.services.mobsf import MobSFClient
from backend.models.schema import AppMetadata


async def static_analysis_node(state: AgentState) -> AgentState:
    logger.info(f"[StaticAnalysis] Starting | scan_id={state.get('scan_id')}")
    state["current_step"] = "static_analysis"
    state.setdefault("errors", [])

    metadata: AppMetadata = state.get("metadata") or AppMetadata(app_name="unknown")
    source_files: list = state.get("source_files", [])
    smali_files: list = state.get("smali_files", [])

    # ── Rule-based SAST ─────────────────────────────────────────────
    try:
        analyzer = StaticAnalyzer(
            source_files=source_files,
            smali_files=smali_files,
            metadata=metadata,
        )
        findings = analyzer.analyze()
        state["static_findings"] = findings
        logger.info(f"[StaticAnalysis] Rule-based: {len(findings)} findings")
    except Exception as e:
        error_msg = f"Static analysis error: {e}"
        logger.error(error_msg)
        state["errors"].append(error_msg)
        state["static_findings"] = []

    # ── MobSF (optional, async) ─────────────────────────────────────
    try:
        mobsf = MobSFClient()
        file_path = state.get("file_path")
        if mobsf.is_configured and file_path and state["input_type"] == "apk":
            report = await mobsf.upload_and_scan(Path(file_path))
            state["mobsf_findings"] = mobsf.parse_mobsf_findings(report)
            logger.info(f"[StaticAnalysis] MobSF: {len(state['mobsf_findings'])} findings")
        else:
            state["mobsf_findings"] = []
    except Exception as e:
        logger.warning(f"[StaticAnalysis] MobSF skipped: {e}")
        state["mobsf_findings"] = []

    return state
