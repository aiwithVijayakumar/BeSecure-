"""
Scan Routes
POST /api/v1/scans        — Upload file and start scan
GET  /api/v1/scans/{id}  — Get scan status
GET  /api/v1/scans        — List all scans
"""
import json
import uuid
from pathlib import Path
from typing import Optional

from fastapi import APIRouter, BackgroundTasks, File, Form, HTTPException, UploadFile, status
from loguru import logger

from backend.config import settings
from backend.models.schema import InputType, ScanResponse, ScanStatus, SecurityReport
from backend.services.file_handler import save_upload
from backend.agents.workflow import run_scan_pipeline, set_progress_callback
from backend.agents.state import AgentState


def _on_step_progress(scan_id: str, step_name: str):
    """Called by the workflow when a new pipeline step starts."""
    entry = _scan_store.get(scan_id)
    if entry:
        entry["current_step"] = step_name


# Register the progress callback
set_progress_callback(_on_step_progress)

router = APIRouter()

# In-memory scan store — persisted to disk for durability
_scan_store: dict[str, dict] = {}


def _persist_report(scan_id: str, entry: dict):
    """Save scan entry (with report) to disk as JSON."""
    try:
        report_path = settings.reports_dir / f"{scan_id}.json"
        report_path.parent.mkdir(parents=True, exist_ok=True)
        with open(report_path, "w", encoding="utf-8") as f:
            json.dump(entry, f, indent=2, default=str)
        logger.info(f"Report persisted to {report_path}")
    except Exception as e:
        logger.error(f"Failed to persist report: {e}")


def _load_persisted_reports():
    """Load all previously saved reports from disk into _scan_store."""
    reports_dir = settings.reports_dir
    if not reports_dir.exists():
        return
    count = 0
    for report_file in reports_dir.glob("*.json"):
        try:
            with open(report_file, "r", encoding="utf-8") as f:
                entry = json.load(f)
            scan_id = entry.get("scan_id", report_file.stem)
            if scan_id not in _scan_store:
                _scan_store[scan_id] = entry
                count += 1
        except Exception as e:
            logger.warning(f"Could not load {report_file.name}: {e}")
    if count:
        logger.info(f"Loaded {count} persisted scan reports from disk")


# Load on module import (server startup)
_load_persisted_reports()


async def _run_scan(scan_id: uuid.UUID, file_path: Path, input_type: str, app_name: str):
    """Background task: run the full LangGraph pipeline."""
    sid = str(scan_id)
    _scan_store[sid]["status"] = ScanStatus.PROCESSING
    _scan_store[sid]["current_step"] = "init"

    work_dir = settings.upload_dir / str(scan_id)
    work_dir.mkdir(parents=True, exist_ok=True)

    initial_state: AgentState = {
        "scan_id": scan_id,
        "app_name": app_name,
        "input_type": input_type,
        "file_path": str(file_path),
        "work_dir": str(work_dir),
        "source_files": [],
        "smali_files": [],
        "static_findings": [],
        "mobsf_findings": [],
        "owasp_context": "",
        "sdlc_context": "",
        "enriched_findings": [],
        "remediated_findings": [],
        "errors": [],
        "current_step": "init",
    }

    try:
        final_state = await run_scan_pipeline(initial_state)
        report: Optional[SecurityReport] = final_state.get("report")

        if report:
            _scan_store[sid]["status"] = ScanStatus.COMPLETED
            _scan_store[sid]["report"] = report.model_dump(mode="json")
            _scan_store[sid]["current_step"] = "completed"
            _persist_report(sid, _scan_store[sid])
            logger.info(f"Scan completed | scan_id={scan_id}")
        else:
            _scan_store[sid]["status"] = ScanStatus.FAILED
            _scan_store[sid]["error"] = "Pipeline produced no report"
            _scan_store[sid]["current_step"] = "failed"

    except Exception as e:
        logger.error(f"Scan pipeline error | scan_id={scan_id}: {e}")
        _scan_store[sid]["status"] = ScanStatus.FAILED
        _scan_store[sid]["error"] = str(e)
        _scan_store[sid]["current_step"] = "failed"


@router.post("/scans", response_model=ScanResponse, status_code=status.HTTP_202_ACCEPTED)
async def create_scan(
    background_tasks: BackgroundTasks,
    file: UploadFile = File(..., description="APK file or source code ZIP"),
    app_name: str = Form(default=""),
    input_type: InputType = Form(default=InputType.APK),
):
    """Upload an APK or source code ZIP and trigger a security scan."""
    scan_id, file_path = await save_upload(file)

    _scan_store[str(scan_id)] = {
        "scan_id": str(scan_id),
        "status": ScanStatus.PENDING,
        "filename": file.filename,
        "app_name": app_name or file.filename,
        "report": None,
        "error": None,
        "current_step": "pending",
    }

    background_tasks.add_task(
        _run_scan,
        scan_id=scan_id,
        file_path=file_path,
        input_type=input_type.value,
        app_name=app_name or file.filename or "Unknown",
    )

    return ScanResponse(
        scan_id=scan_id,
        status=ScanStatus.PENDING,
        message="Scan submitted. Poll /api/v1/scans/{scan_id} for status.",
    )


@router.get("/scans/{scan_id}")
async def get_scan_status(scan_id: str):
    """Get the current status (and report if completed) for a scan."""
    entry = _scan_store.get(scan_id)
    if not entry:
        raise HTTPException(status_code=404, detail=f"Scan '{scan_id}' not found")

    return {
        "scan_id": scan_id,
        "status": entry["status"],
        "app_name": entry.get("app_name"),
        "error": entry.get("error"),
        "current_step": entry.get("current_step", "unknown"),
        "report": entry.get("report"),  # None until completed
    }


@router.get("/scans", summary="List all scans")
async def list_scans():
    """Return a summary list of all scans (without full reports)."""
    return [
        {
            "scan_id": v["scan_id"],
            "status": v["status"],
            "app_name": v.get("app_name"),
            "filename": v.get("filename"),
        }
        for v in _scan_store.values()
    ]


@router.delete("/scans/{scan_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_scan(scan_id: str):
    """Remove a scan from the store."""
    if scan_id not in _scan_store:
        raise HTTPException(status_code=404, detail="Scan not found")
    del _scan_store[scan_id]
    # Also remove persisted file
    report_path = settings.reports_dir / f"{scan_id}.json"
    report_path.unlink(missing_ok=True)
