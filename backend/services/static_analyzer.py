"""
Static Analysis Engine
Combines regex rules + pattern matching to identify:
  - Hardcoded secrets / API keys
  - Insecure storage (SharedPreferences, SQLite, files)
  - Weak cryptography (MD5, DES, ECB mode)
  - WebView vulnerabilities
  - Exported components without permission
  - SSL/TLS misconfigurations
  - SQL injection patterns
  - Debug flags
"""
import re
from pathlib import Path
from typing import List
from uuid import uuid4

from loguru import logger

from backend.models.schema import (
    OWASPCategory, Severity, Vulnerability, AppMetadata
)

# ─── Rule Definitions ──────────────────────────────────────────────────────────
RULES = [
    {
        "id": "SA001",
        "title": "Hardcoded Secret / API Key",
        "severity": Severity.CRITICAL,
        "owasp": OWASPCategory.M1_IMPROPER_CREDENTIAL_USAGE,
        "cwe": "CWE-798",
        "pattern": re.compile(
            r'(api[_-]?key|secret|password|passwd|token|bearer|aws_secret|private_key)'
            r'\s*[=:]\s*["\']([^"\']{8,})["\']',
            re.MULTILINE | re.IGNORECASE,
        ),
        "fix": "Move secrets to Android Keystore or encrypted environment configs. Never hardcode credentials in source code.",
        "sdlc_phase": "coding",
    },
    {
        "id": "SA002",
        "title": "Insecure SharedPreferences Storage",
        "severity": Severity.HIGH,
        "owasp": OWASPCategory.M9_INSECURE_DATA_STORAGE,
        "cwe": "CWE-312",
        "pattern": re.compile(
            r'getSharedPreferences\s*\(.*MODE_WORLD_READABLE|MODE_WORLD_WRITEABLE',
            re.MULTILINE,
        ),
        "fix": "Use MODE_PRIVATE for SharedPreferences. Consider EncryptedSharedPreferences for sensitive data.",
        "sdlc_phase": "coding",
    },
    {
        "id": "SA003",
        "title": "Weak Cryptography — MD5 / SHA1",
        "severity": Severity.HIGH,
        "owasp": OWASPCategory.M10_INSUFFICIENT_CRYPTOGRAPHY,
        "cwe": "CWE-327",
        "pattern": re.compile(
            r'MessageDigest\.getInstance\s*\(\s*["\'](md5|sha-?1)["\']',
            re.MULTILINE | re.IGNORECASE,
        ),
        "fix": "Replace MD5/SHA-1 with SHA-256 or SHA-3. For password hashing use Argon2 or bcrypt.",
        "sdlc_phase": "coding",
    },
    {
        "id": "SA004",
        "title": "Weak Cryptography — DES / RC4 Cipher",
        "severity": Severity.CRITICAL,
        "owasp": OWASPCategory.M10_INSUFFICIENT_CRYPTOGRAPHY,
        "cwe": "CWE-327",
        "pattern": re.compile(
            r'Cipher\.getInstance\s*\(\s*["\'](DES|RC4|RC2|Blowfish)["\']',
            re.MULTILINE | re.IGNORECASE,
        ),
        "fix": "Use AES-256-GCM instead of DES/RC4. Ensure proper IV generation and authenticated encryption.",
        "sdlc_phase": "coding",
    },
    {
        "id": "SA005",
        "title": "ECB Cipher Mode (No IV)",
        "severity": Severity.HIGH,
        "owasp": OWASPCategory.M10_INSUFFICIENT_CRYPTOGRAPHY,
        "cwe": "CWE-330",
        "pattern": re.compile(
            r'Cipher\.getInstance\s*\(\s*["\'][^"\']*\/ECB\/[^"\']*["\']',
            re.MULTILINE,
        ),
        "fix": "Replace ECB mode with GCM or CBC with a random IV. ECB does not provide semantic security.",
        "sdlc_phase": "coding",
    },
    {
        "id": "SA006",
        "title": "WebView JavaScript Enabled",
        "severity": Severity.HIGH,
        "owasp": OWASPCategory.M4_INSUFFICIENT_INPUT_VALIDATION,
        "cwe": "CWE-749",
        "pattern": re.compile(
            r'setJavaScriptEnabled\s*\(\s*true\s*\)',
            re.MULTILINE,
        ),
        "fix": "Disable JavaScript unless absolutely required. If needed, validate all JS interfaces and use Content Security Policies.",
        "sdlc_phase": "coding",
    },
    {
        "id": "SA007",
        "title": "WebView File Access Enabled",
        "severity": Severity.HIGH,
        "owasp": OWASPCategory.M9_INSECURE_DATA_STORAGE,
        "cwe": "CWE-200",
        "pattern": re.compile(
            r'setAllowFileAccess\s*\(\s*true\s*\)|setAllowFileAccessFromFileURLs\s*\(\s*true\s*\)',
            re.MULTILINE,
        ),
        "fix": "Disable file access in WebView. Use setAllowFileAccess(false) and setAllowFileAccessFromFileURLs(false).",
        "sdlc_phase": "coding",
    },
    {
        "id": "SA008",
        "title": "SSL/TLS Certificate Validation Disabled",
        "severity": Severity.CRITICAL,
        "owasp": OWASPCategory.M5_INSECURE_COMMUNICATION,
        "cwe": "CWE-295",
        "pattern": re.compile(
            r'onReceivedSslError.*proceed\s*\(\s*\)|ALLOW_ALL_HOSTNAME_VERIFIER|'
            r'hostnameVerifier.*return\s+true|TrustAllCerts|NullX509TrustManager',
            re.MULTILINE | re.DOTALL,
        ),
        "fix": "Never bypass SSL certificate validation. Implement proper certificate pinning using OkHttp CertificatePinner.",
        "sdlc_phase": "coding",
    },
    {
        "id": "SA009",
        "title": "Insecure Random Number Generator",
        "severity": Severity.MEDIUM,
        "owasp": OWASPCategory.M10_INSUFFICIENT_CRYPTOGRAPHY,
        "cwe": "CWE-338",
        "pattern": re.compile(
            r'\bnew\s+Random\s*\(\s*\)',
            re.MULTILINE,
        ),
        "fix": "Replace java.util.Random with java.security.SecureRandom for cryptographic purposes.",
        "sdlc_phase": "coding",
    },
    {
        "id": "SA010",
        "title": "SQL Injection Risk — Dynamic Query",
        "severity": Severity.CRITICAL,
        "owasp": OWASPCategory.M4_INSUFFICIENT_INPUT_VALIDATION,
        "cwe": "CWE-89",
        "pattern": re.compile(
            r'rawQuery\s*\([^,]*\+|execSQL\s*\([^,]*\+',
            re.MULTILINE,
        ),
        "fix": "Use parameterized queries or Room ORM. Never concatenate user input into SQL strings.",
        "sdlc_phase": "coding",
    },
    {
        "id": "SA011",
        "title": "Externally Stored Sensitive File",
        "severity": Severity.HIGH,
        "owasp": OWASPCategory.M9_INSECURE_DATA_STORAGE,
        "cwe": "CWE-312",
        "pattern": re.compile(
            r'getExternalStorageDirectory|getExternalFilesDir|Environment\.DIRECTORY',
            re.MULTILINE,
        ),
        "fix": "Store sensitive data in internal storage using getFilesDir() or use EncryptedFile from Jetpack Security.",
        "sdlc_phase": "coding",
    },
    {
        "id": "SA012",
        "title": "Log Sensitive Data",
        "severity": Severity.MEDIUM,
        "owasp": OWASPCategory.M6_INADEQUATE_PRIVACY,
        "cwe": "CWE-532",
        "pattern": re.compile(
            r'Log\.[dievw]\s*\([^)]*(password|token|secret|key|credential)',
            re.MULTILINE | re.IGNORECASE,
        ),
        "fix": "Never log sensitive information. Use ProGuard/R8 to strip log calls in release builds.",
        "sdlc_phase": "coding",
    },
    {
        "id": "SA013",
        "title": "Intent with Sensitive Data — Implicit",
        "severity": Severity.MEDIUM,
        "owasp": OWASPCategory.M8_SECURITY_MISCONFIGURATION,
        "cwe": "CWE-927",
        "pattern": re.compile(
            r'new\s+Intent\s*\(\s*\)|Intent\.ACTION_SEND',
            re.MULTILINE,
        ),
        "fix": "Use explicit Intents with specific component names. Avoid broadcasting sensitive data via implicit Intents.",
        "sdlc_phase": "coding",
    },
    {
        "id": "SA014",
        "title": "Debuggable Build Flag Detected",
        "severity": Severity.HIGH,
        "owasp": OWASPCategory.M7_INSUFFICIENT_BINARY_PROTECTION,
        "cwe": "CWE-489",
        "pattern": re.compile(
            r'android:debuggable\s*=\s*["\']true["\']',
            re.MULTILINE,
        ),
        "fix": "Set android:debuggable=\"false\" for production builds. Use BuildConfig.DEBUG for conditional debug code.",
        "sdlc_phase": "deployment",
    },
]


class StaticAnalyzer:
    """
    Scans a list of source/smali files against the rule set.
    Returns a list of Vulnerability objects.
    """

    def __init__(self, source_files: List[str], smali_files: List[str], metadata: AppMetadata):
        self.source_files = source_files
        self.smali_files = smali_files
        self.metadata = metadata

    def analyze(self) -> List[Vulnerability]:
        findings: List[Vulnerability] = []

        # Run regex rules on source code
        all_files = self.source_files + self.smali_files
        for file_path in all_files:
            file_findings = self._scan_file(Path(file_path))
            findings.extend(file_findings)

        # Manifest-based checks
        findings.extend(self._check_manifest())

        logger.info(f"Static analysis complete: {len(findings)} findings")
        return findings

    def _scan_file(self, path: Path) -> List[Vulnerability]:
        results = []
        try:
            content = path.read_text(encoding="utf-8", errors="ignore")
        except Exception:
            return results

        for rule in RULES:
            for match in rule["pattern"].finditer(content):
                line_number = content[: match.start()].count("\n") + 1
                evidence = match.group(0)[:200]

                results.append(Vulnerability(
                    id=uuid4(),
                    title=rule["title"],
                    severity=rule["severity"],
                    owasp_category=rule["owasp"],
                    cwe_id=rule.get("cwe"),
                    description=f"Detected '{rule['title']}' pattern in {path.name} at line {line_number}.",
                    evidence=evidence,
                    file_path=str(path),
                    line_number=line_number,
                    fix=rule["fix"],
                    sdlc_phase=rule.get("sdlc_phase"),
                ))
        return results

    def _check_manifest(self) -> List[Vulnerability]:
        """Run manifest-level security checks."""
        findings = []

        if self.metadata.debuggable:
            findings.append(Vulnerability(
                id=uuid4(),
                title="Debuggable Flag Enabled (Manifest)",
                severity=Severity.HIGH,
                owasp_category=OWASPCategory.M7_INSUFFICIENT_BINARY_PROTECTION,
                cwe_id="CWE-489",
                description="The application manifest has android:debuggable=true, exposing debug attack surface.",
                evidence='android:debuggable="true"',
                fix="Set android:debuggable=\"false\" or remove it (defaults to false) in production.",
                sdlc_phase="deployment",
            ))

        if self.metadata.backup_enabled:
            findings.append(Vulnerability(
                id=uuid4(),
                title="Android Backup Enabled (Manifest)",
                severity=Severity.MEDIUM,
                owasp_category=OWASPCategory.M9_INSECURE_DATA_STORAGE,
                cwe_id="CWE-312",
                description="allowBackup=true allows ADB backup of app data without device encryption.",
                evidence='android:allowBackup="true"',
                fix="Set android:allowBackup=\"false\" unless backup is required. Use encrypted backup rules.",
                sdlc_phase="deployment",
            ))

        if self.metadata.uses_cleartext_traffic:
            findings.append(Vulnerability(
                id=uuid4(),
                title="Cleartext Traffic Permitted (Manifest)",
                severity=Severity.HIGH,
                owasp_category=OWASPCategory.M5_INSECURE_COMMUNICATION,
                cwe_id="CWE-319",
                description="usesCleartextTraffic=true allows plain HTTP connections.",
                evidence='android:usesCleartextTraffic="true"',
                fix="Set usesCleartextTraffic to false and use HTTPS for all network calls.",
                sdlc_phase="deployment",
            ))

        for perm in self.metadata.permissions:
            if perm.is_dangerous:
                findings.append(Vulnerability(
                    id=uuid4(),
                    title=f"Dangerous Permission Requested: {perm.name.split('.')[-1]}",
                    severity=Severity.LOW,
                    owasp_category=OWASPCategory.M8_SECURITY_MISCONFIGURATION,
                    description=f"App requests dangerous permission: {perm.name}",
                    evidence=f"<uses-permission android:name=\"{perm.name}\" />",
                    fix="Only request permissions that are strictly necessary. Apply the principle of least privilege.",
                    sdlc_phase="design",
                ))

        return findings
