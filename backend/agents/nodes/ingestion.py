"""
Ingestion Agent Node
Decompiles APK or processes source code zip/files.
Populates: metadata, source_files, smali_files, jadx_out, apktool_out
"""
import tarfile
import zipfile
from pathlib import Path
from loguru import logger

from backend.agents.state import AgentState
from backend.services.decompiler import DecompilerService
from backend.models.schema import InputType, AppMetadata


# File extensions we care about for static analysis
SOURCE_EXTENSIONS = {
    ".java", ".kt", ".xml", ".gradle", ".properties",
    ".json", ".yml", ".yaml", ".cfg", ".pro",
}


# Paths/dirs to skip during extraction (build artifacts, caches, etc.)
SKIP_PATTERNS = {
    ".dex", ".class", ".jar", ".aar", ".so",
    "__pycache__", ".gradle", "build/intermediates",
    "build/.transforms", "build/generated",
    ".git", "node_modules",
}


def _should_skip(name: str) -> bool:
    """Check if a zip member should be skipped during extraction."""
    name_lower = name.lower().replace("\\", "/")
    for pat in SKIP_PATTERNS:
        if pat in name_lower:
            return True
    return False


def _extract_archive(archive_path: Path, dest_dir: Path) -> bool:
    """Extract a ZIP or tar.gz archive to dest_dir. Returns True on success."""
    dest_dir.mkdir(parents=True, exist_ok=True)

    if zipfile.is_zipfile(archive_path):
        logger.info(f"[Ingestion] Extracting ZIP: {archive_path.name}")
        extracted = 0
        skipped = 0
        errors = 0
        with zipfile.ZipFile(archive_path, "r") as zf:
            for member in zf.namelist():
                if _should_skip(member):
                    skipped += 1
                    continue
                try:
                    zf.extract(member, dest_dir)
                    extracted += 1
                except Exception as e:
                    errors += 1
                    if errors <= 3:
                        logger.warning(f"[Ingestion] Skip extract '{member[:80]}': {e}")
        logger.info(
            f"[Ingestion] ZIP extracted: {extracted} files, "
            f"{skipped} skipped, {errors} errors"
        )
        return extracted > 0

    if tarfile.is_tarfile(archive_path):
        logger.info(f"[Ingestion] Extracting tar archive: {archive_path.name}")
        extracted = 0
        with tarfile.open(archive_path, "r:*") as tf:
            for member in tf.getmembers():
                if _should_skip(member.name):
                    continue
                try:
                    tf.extract(member, dest_dir)
                    extracted += 1
                except Exception:
                    pass
        return extracted > 0

    return False


def _collect_source_files(search_dir: Path) -> list[str]:
    """Recursively collect all source/config files from a directory."""
    files = []
    if not search_dir.exists():
        return files
    for p in search_dir.rglob("*"):
        if p.is_file() and p.suffix.lower() in SOURCE_EXTENSIONS:
            files.append(str(p))
    return files


def _find_manifest(search_dir: Path):
    """Locate AndroidManifest.xml in extracted source tree."""
    for p in search_dir.rglob("AndroidManifest.xml"):
        return p
    return None


def _parse_manifest_for_metadata(manifest_path: Path, app_name: str) -> AppMetadata:
    """Parse AndroidManifest.xml for app metadata (reuse decompiler logic)."""
    try:
        from backend.services.decompiler import DecompilerService
        import xml.etree.ElementTree as ET
        from backend.models.schema import AppPermission

        DANGEROUS_PERMISSIONS = {
            "android.permission.READ_CONTACTS",
            "android.permission.WRITE_CONTACTS",
            "android.permission.READ_CALL_LOG",
            "android.permission.READ_SMS",
            "android.permission.RECEIVE_SMS",
            "android.permission.SEND_SMS",
            "android.permission.CAMERA",
            "android.permission.RECORD_AUDIO",
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.ACCESS_COARSE_LOCATION",
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.PROCESS_OUTGOING_CALLS",
            "android.permission.READ_PHONE_STATE",
            "android.permission.BLUETOOTH_ADMIN",
            "android.permission.SYSTEM_ALERT_WINDOW",
            "android.permission.GET_ACCOUNTS",
            "android.permission.USE_BIOMETRIC",
        }

        tree = ET.parse(manifest_path)
        root = tree.getroot()
        ns = {"android": "http://schemas.android.com/apk/res/android"}
        package = root.get("package", "unknown")
        version_name = root.get("{http://schemas.android.com/apk/res/android}versionName")
        version_code = root.get("{http://schemas.android.com/apk/res/android}versionCode")

        sdk_el = root.find("uses-sdk")
        target_sdk = int(sdk_el.get("{http://schemas.android.com/apk/res/android}targetSdkVersion", 0)) if sdk_el is not None else None
        min_sdk = int(sdk_el.get("{http://schemas.android.com/apk/res/android}minSdkVersion", 0)) if sdk_el is not None else None

        permissions = []
        for perm_el in root.findall("uses-permission"):
            name = perm_el.get("{http://schemas.android.com/apk/res/android}name", "")
            permissions.append(AppPermission(
                name=name,
                is_dangerous=name in DANGEROUS_PERMISSIONS,
            ))

        app_el = root.find("application")
        debuggable = app_el.get("{http://schemas.android.com/apk/res/android}debuggable", "false").lower() == "true" if app_el is not None else False
        backup = app_el.get("{http://schemas.android.com/apk/res/android}allowBackup", "true").lower() == "true" if app_el is not None else True
        cleartext = app_el.get("{http://schemas.android.com/apk/res/android}usesCleartextTraffic", "false").lower() == "true" if app_el is not None else False

        def get_exported(tag: str) -> list[str]:
            result = []
            if app_el is None:
                return result
            for el in app_el.findall(tag):
                exported = el.get("{http://schemas.android.com/apk/res/android}exported", "false").lower()
                if exported == "true":
                    result.append(el.get("{http://schemas.android.com/apk/res/android}name", "unknown"))
            return result

        return AppMetadata(
            app_name=package.split(".")[-1] or app_name,
            package_name=package,
            version_name=version_name,
            version_code=version_code,
            target_sdk=target_sdk,
            min_sdk=min_sdk,
            permissions=permissions,
            exported_activities=get_exported("activity"),
            exported_services=get_exported("service"),
            exported_receivers=get_exported("receiver"),
            exported_providers=get_exported("provider"),
            debuggable=debuggable,
            backup_enabled=backup,
            uses_cleartext_traffic=cleartext,
        )
    except Exception as e:
        logger.warning(f"[Ingestion] Manifest parse failed: {e}")
        return AppMetadata(app_name=app_name)


async def ingestion_node(state: AgentState) -> AgentState:
    logger.info(f"[Ingestion] Starting | scan_id={state.get('scan_id')}")
    state["current_step"] = "ingestion"
    state.setdefault("errors", [])

    file_path = Path(state["file_path"])
    work_dir = Path(state["work_dir"])

    try:
        if state["input_type"] == InputType.APK or state["input_type"] == "apk":
            svc = DecompilerService(apk_path=file_path, work_dir=work_dir)
            result = await svc.decompile()

            state["metadata"] = result.get("metadata") or AppMetadata(app_name=state.get("app_name", "unknown"))
            state["source_files"] = result.get("source_files", [])
            state["smali_files"] = result.get("smali_files", [])
            state["jadx_out"] = result.get("jadx_out")
            state["apktool_out"] = result.get("apktool_out")
            state["manifest_path"] = result.get("manifest_path")
            logger.info(
                f"[Ingestion] Decompiled: {len(state['source_files'])} src files, "
                f"{len(state['smali_files'])} smali files"
            )

        else:
            # ── Source code (zip/tar.gz/directory) ─────────────────────
            extract_dir = work_dir / "extracted"

            # Step 1: Extract archive if it's a zip/tar.gz
            if file_path.suffix.lower() in (".zip", ".gz", ".tgz"):
                extracted = _extract_archive(file_path, extract_dir)
                if not extracted:
                    state["errors"].append(f"Could not extract archive: {file_path.name}")
                    logger.error(f"[Ingestion] Failed to extract archive: {file_path.name}")
                    extract_dir = file_path.parent
                else:
                    logger.info(f"[Ingestion] Archive extracted to {extract_dir}")
            else:
                # Single file or directory — search relative to the file
                extract_dir = file_path.parent

            # Step 2: Collect all source files from extracted directory
            source_files = _collect_source_files(extract_dir)
            state["source_files"] = source_files
            state["smali_files"] = []

            # Step 3: Look for AndroidManifest.xml for metadata
            manifest = _find_manifest(extract_dir)
            if manifest:
                state["manifest_path"] = str(manifest)
                state["metadata"] = _parse_manifest_for_metadata(
                    manifest, state.get("app_name", "unknown")
                )
                logger.info(f"[Ingestion] Found AndroidManifest.xml: {manifest}")
            else:
                state["metadata"] = AppMetadata(app_name=state.get("app_name", "unknown"))
                logger.info("[Ingestion] No AndroidManifest.xml found in source code")

            logger.info(f"[Ingestion] Source code: {len(source_files)} files collected")

    except Exception as e:
        error_msg = f"Ingestion error: {e}"
        logger.error(error_msg)
        state["errors"].append(error_msg)
        state["source_files"] = []
        state["smali_files"] = []
        state["metadata"] = AppMetadata(app_name=state.get("app_name", "unknown"))

    return state
