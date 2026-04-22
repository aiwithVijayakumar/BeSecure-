"""
LangGraph Agent State
All agents read from and write to this shared TypedDict.
"""
from __future__ import annotations

from typing import Any, Dict, List, Optional
from uuid import UUID
from typing_extensions import TypedDict

from backend.models.schema import (
    AppMetadata, SecurityReport, Vulnerability, ThreatModel, RiskScore
)


class AgentState(TypedDict, total=False):
    # ── Scan identity ────────────────────────────────────────────────
    scan_id: UUID
    app_name: str
    input_type: str            # "apk" | "source_code"
    file_path: str             # Absolute path to uploaded file
    work_dir: str              # Scratch directory for this scan

    # ── Ingestion outputs ────────────────────────────────────────────
    metadata: Optional[AppMetadata]
    source_files: List[str]
    smali_files: List[str]
    jadx_out: Optional[str]
    apktool_out: Optional[str]
    manifest_path: Optional[str]
    decompile_error: Optional[str]

    # ── Static analysis outputs ──────────────────────────────────────
    static_findings: List[Vulnerability]
    mobsf_findings: List[Dict[str, Any]]

    # ── RAG / Knowledge outputs ──────────────────────────────────────
    owasp_context: str          # Retrieved OWASP passages
    sdlc_context: str           # Retrieved SDLC passages
    enriched_findings: List[Vulnerability]

    # ── Threat modelling outputs ─────────────────────────────────────
    threat_model: Optional[ThreatModel]

    # ── Risk scoring outputs ─────────────────────────────────────────
    risk_score: Optional[RiskScore]

    # ── Remediation outputs ──────────────────────────────────────────
    remediated_findings: List[Vulnerability]

    # ── Final report ─────────────────────────────────────────────────
    report: Optional[SecurityReport]

    # ── Control flow ─────────────────────────────────────────────────
    errors: List[str]
    current_step: str
