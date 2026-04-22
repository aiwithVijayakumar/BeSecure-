"""
Secure file upload handler.
- Validates file type & size
- Saves to UPLOAD_DIR/<scan_id>/
- Returns paths for downstream agents
"""
import shutil
import uuid
from pathlib import Path
from typing import Tuple

import aiofiles
from fastapi import HTTPException, UploadFile, status
from loguru import logger

from backend.config import settings

ALLOWED_EXTENSIONS = {".apk", ".zip", ".tar.gz", ".java", ".kt"}
MAX_BYTES = settings.max_upload_size_mb * 1024 * 1024


async def save_upload(file: UploadFile) -> Tuple[uuid.UUID, Path]:
    """
    Validate and persist an uploaded file.
    Returns (scan_id, file_path).
    """
    filename = file.filename or "unknown"
    ext = Path(filename).suffix.lower()

    if ext not in ALLOWED_EXTENSIONS:
        raise HTTPException(
            status_code=status.HTTP_415_UNSUPPORTED_MEDIA_TYPE,
            detail=f"Unsupported file type '{ext}'. Allowed: {ALLOWED_EXTENSIONS}",
        )

    scan_id = uuid.uuid4()
    dest_dir = settings.upload_dir / str(scan_id)
    dest_dir.mkdir(parents=True, exist_ok=True)
    dest_path = dest_dir / filename

    size = 0
    async with aiofiles.open(dest_path, "wb") as out:
        while chunk := await file.read(1024 * 1024):  # 1 MB chunks
            size += len(chunk)
            if size > MAX_BYTES:
                dest_path.unlink(missing_ok=True)
                raise HTTPException(
                    status_code=status.HTTP_413_REQUEST_ENTITY_TOO_LARGE,
                    detail=f"File exceeds maximum size of {settings.max_upload_size_mb} MB",
                )
            await out.write(chunk)

    logger.info(f"Saved upload: {dest_path} ({size / 1024:.1f} KB) | scan_id={scan_id}")
    return scan_id, dest_path


def cleanup_upload(scan_id: uuid.UUID):
    """Remove the upload directory for a scan (call after report is generated)."""
    upload_dir = settings.upload_dir / str(scan_id)
    if upload_dir.exists():
        shutil.rmtree(upload_dir)
        logger.info(f"Cleaned up upload dir for scan_id={scan_id}")
