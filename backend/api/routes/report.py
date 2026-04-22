"""
Report Routes
GET /api/v1/reports/{scan_id}           — Full JSON report
GET /api/v1/reports/{scan_id}/summary   — Executive summary only
"""
import json
from fastapi import APIRouter, HTTPException
from backend.api.routes.scan import _scan_store
from backend.models.schema import ScanStatus
from backend.config import settings

router = APIRouter()


def _load_report_from_disk(scan_id: str) -> dict | None:
    """Try to load a report from persisted JSON on disk."""
    report_path = settings.reports_dir / f"{scan_id}.json"
    if report_path.exists():
        try:
            with open(report_path, "r", encoding="utf-8") as f:
                entry = json.load(f)
            # Cache it back into memory
            _scan_store[scan_id] = entry
            return entry
        except Exception:
            pass
    return None


@router.get("/reports/{scan_id}")
async def get_report(scan_id: str):
    """Return the full security report for a completed scan."""
    entry = _scan_store.get(scan_id) or _load_report_from_disk(scan_id)
    if not entry:
        raise HTTPException(status_code=404, detail="Scan not found")
    if entry["status"] != ScanStatus.COMPLETED and entry["status"] != "completed":
        raise HTTPException(status_code=400, detail=f"Scan is not yet complete. Status: {entry['status']}")
    report = entry.get("report")
    if not report:
        raise HTTPException(status_code=404, detail="Report not available")
    return report


@router.get("/reports/{scan_id}/summary")
async def get_report_summary(scan_id: str):
    """Return a lightweight executive summary for a completed scan."""
    entry = _scan_store.get(scan_id) or _load_report_from_disk(scan_id)
    if not entry:
        raise HTTPException(status_code=404, detail="Scan not found")
    if entry["status"] != ScanStatus.COMPLETED and entry["status"] != "completed":
        raise HTTPException(status_code=400, detail=f"Scan status: {entry['status']}")

    report = entry.get("report", {})
    return {
        "scan_id": scan_id,
        "app_name": report.get("app_name"),
        "risk_score": report.get("risk_score"),
        "vuln_summary": report.get("vuln_summary"),
        "summary": report.get("summary"),
        "owasp_compliance": report.get("owasp_compliance"),
    }
