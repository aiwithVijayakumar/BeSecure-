from __future__ import annotations

from datetime import datetime
from enum import Enum
from typing import List, Optional
from uuid import UUID, uuid4

from pydantic import BaseModel, Field


# ─── Enums ─────────────────────────────────────────────────────────────────────

class Severity(str, Enum):
    CRITICAL = "Critical"
    HIGH = "High"
    MEDIUM = "Medium"
    LOW = "Low"
    INFO = "Info"


class ScanStatus(str, Enum):
    PENDING = "pending"
    PROCESSING = "processing"
    COMPLETED = "completed"
    FAILED = "failed"


class InputType(str, Enum):
    APK = "apk"
    SOURCE_CODE = "source_code"


class OWASPCategory(str, Enum):
    M1_IMPROPER_CREDENTIAL_USAGE = "M1 - Improper Credential Usage"
    M2_INADEQUATE_SUPPLY_CHAIN = "M2 - Inadequate Supply Chain Security"
    M3_INSECURE_AUTHENTICATION = "M3 - Insecure Authentication/Authorization"
    M4_INSUFFICIENT_INPUT_VALIDATION = "M4 - Insufficient Input/Output Validation"
    M5_INSECURE_COMMUNICATION = "M5 - Insecure Communication"
    M6_INADEQUATE_PRIVACY = "M6 - Inadequate Privacy Controls"
    M7_INSUFFICIENT_BINARY_PROTECTION = "M7 - Insufficient Binary Protections"
    M8_SECURITY_MISCONFIGURATION = "M8 - Security Misconfiguration"
    M9_INSECURE_DATA_STORAGE = "M9 - Insecure Data Storage"
    M10_INSUFFICIENT_CRYPTOGRAPHY = "M10 - Insufficient Cryptography"
    UNKNOWN = "Unknown"


# ─── Scan Request / Response ────────────────────────────────────────────────────

class ScanCreate(BaseModel):
    """Submitted when a user uploads an APK or source code."""
    filename: str
    input_type: InputType
    app_name: Optional[str] = None
    notes: Optional[str] = None


class ScanResponse(BaseModel):
    scan_id: UUID
    status: ScanStatus
    message: str
    created_at: datetime = Field(default_factory=datetime.utcnow)


# ─── Vulnerability ──────────────────────────────────────────────────────────────

class CodeFix(BaseModel):
    language: str = "java"
    vulnerable_code: Optional[str] = None
    fixed_code: str
    explanation: str


class Vulnerability(BaseModel):
    id: UUID = Field(default_factory=uuid4)
    title: str
    severity: Severity
    owasp_category: OWASPCategory
    cwe_id: Optional[str] = None          # e.g. "CWE-89"
    description: str
    evidence: str                          # code snippet or file path
    file_path: Optional[str] = None
    line_number: Optional[int] = None
    attack_vector: Optional[str] = None
    impact: Optional[str] = None
    fix: str                               # Short remediation description
    code_fix: Optional[CodeFix] = None
    sdlc_phase: Optional[str] = None       # design / coding / deployment
    references: List[str] = Field(default_factory=list)


# ─── Threat Model ───────────────────────────────────────────────────────────────

class ThreatScenario(BaseModel):
    threat_actor: str
    attack_vector: str
    scenario: str
    impact: str
    likelihood: Severity


class ThreatModel(BaseModel):
    attack_surface: List[str] = Field(default_factory=list)
    scenarios: List[ThreatScenario] = Field(default_factory=list)
    data_exposure_risks: List[str] = Field(default_factory=list)


# ─── Risk Score ─────────────────────────────────────────────────────────────────

class RiskScore(BaseModel):
    overall: Severity
    score: float = Field(ge=0, le=10)      # CVSS-style numeric score 0-10
    exploitability: float = Field(ge=0, le=10)
    impact: float = Field(ge=0, le=10)
    business_risk: float = Field(ge=0, le=10)
    rationale: str


# ─── App Metadata ───────────────────────────────────────────────────────────────

class AppPermission(BaseModel):
    name: str
    is_dangerous: bool
    description: Optional[str] = None


class AppMetadata(BaseModel):
    app_name: str
    package_name: Optional[str] = None
    version_name: Optional[str] = None
    version_code: Optional[str] = None
    target_sdk: Optional[int] = None
    min_sdk: Optional[int] = None
    permissions: List[AppPermission] = Field(default_factory=list)
    exported_activities: List[str] = Field(default_factory=list)
    exported_services: List[str] = Field(default_factory=list)
    exported_receivers: List[str] = Field(default_factory=list)
    exported_providers: List[str] = Field(default_factory=list)
    uses_cleartext_traffic: bool = False
    debuggable: bool = False
    backup_enabled: bool = False


# ─── Final Report ───────────────────────────────────────────────────────────────

class VulnerabilitySummary(BaseModel):
    critical: int = 0
    high: int = 0
    medium: int = 0
    low: int = 0
    info: int = 0
    total: int = 0


class DeveloperChecklist(BaseModel):
    items: List[str] = Field(default_factory=list)


class SecurityReport(BaseModel):
    scan_id: UUID = Field(default_factory=uuid4)
    app_name: str
    input_type: InputType
    status: ScanStatus = ScanStatus.COMPLETED
    created_at: datetime = Field(default_factory=datetime.utcnow)
    completed_at: Optional[datetime] = None

    # Core content
    metadata: Optional[AppMetadata] = None
    risk_score: RiskScore
    summary: str
    vulnerabilities: List[Vulnerability] = Field(default_factory=list)
    vuln_summary: VulnerabilitySummary = Field(default_factory=VulnerabilitySummary)
    threat_model: Optional[ThreatModel] = None
    developer_checklist: Optional[DeveloperChecklist] = None
    owasp_compliance: dict = Field(default_factory=dict)   # category → pass/fail

    class Config:
        use_enum_values = True
