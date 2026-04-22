"""
MobSF REST API wrapper.
If MOBSF_API_KEY is set, this augments our local static analysis
with MobSF's deep scan results.
"""
import httpx
from pathlib import Path
from loguru import logger
from backend.config import settings


class MobSFClient:
    def __init__(self):
        self.base_url = settings.mobsf_url.rstrip("/")
        self.api_key = settings.mobsf_api_key
        self.headers = {"Authorization": self.api_key}

    @property
    def is_configured(self) -> bool:
        return bool(self.api_key)

    async def upload_and_scan(self, apk_path: Path) -> dict:
        """Upload APK to MobSF and trigger static analysis."""
        if not self.is_configured:
            logger.info("MobSF not configured — skipping MobSF scan")
            return {}

        try:
            async with httpx.AsyncClient(timeout=120) as client:
                # Upload
                with open(apk_path, "rb") as f:
                    upload_resp = await client.post(
                        f"{self.base_url}/api/v1/upload",
                        headers=self.headers,
                        files={"file": (apk_path.name, f, "application/octet-stream")},
                    )
                upload_resp.raise_for_status()
                upload_data = upload_resp.json()
                scan_hash = upload_data.get("hash")

                if not scan_hash:
                    logger.warning("MobSF upload returned no hash")
                    return {}

                # Scan
                scan_resp = await client.post(
                    f"{self.base_url}/api/v1/scan",
                    headers=self.headers,
                    data={"scan_type": "apk", "file_name": apk_path.name, "hash": scan_hash},
                )
                scan_resp.raise_for_status()

                # Report
                report_resp = await client.post(
                    f"{self.base_url}/api/v1/report_json",
                    headers=self.headers,
                    data={"hash": scan_hash},
                )
                report_resp.raise_for_status()
                return report_resp.json()

        except httpx.HTTPError as e:
            logger.error(f"MobSF HTTP error: {e}")
            return {}
        except Exception as e:
            logger.error(f"MobSF error: {e}")
            return {}

    def parse_mobsf_findings(self, report: dict) -> list:
        """Convert MobSF JSON report to our Vulnerability schema keys."""
        findings = []
        for severity_key in ["high", "warning", "info", "secure"]:
            for item in report.get(severity_key, {}).values():
                findings.append({
                    "title": item.get("title", "Unknown"),
                    "severity": severity_key,
                    "description": item.get("description", ""),
                    "source": "MobSF",
                })
        return findings
