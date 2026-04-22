"""
APK Decompiler Service
Orchestrates JADX and APKTool to extract:
  - Decompiled Java/Kotlin source
  - AndroidManifest.xml
  - Permissions, exported components
  - Smali code (APKTool)
"""
import asyncio
import re
import subprocess
import xml.etree.ElementTree as ET
from pathlib import Path
from typing import List, Optional

from loguru import logger

from backend.config import settings
from backend.models.schema import AppMetadata, AppPermission


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


class DecompilerService:
    def __init__(self, apk_path: Path, work_dir: Path):
        self.apk_path = apk_path
        self.work_dir = work_dir
        self.jadx_out = work_dir / "jadx_out"
        self.apktool_out = work_dir / "apktool_out"

    async def decompile(self) -> dict:
        """Run JADX + APKTool concurrently and return extracted data."""
        logger.info(f"Decompiling APK: {self.apk_path.name}")

        jadx_task = asyncio.create_task(self._run_jadx())
        apktool_task = asyncio.create_task(self._run_apktool())

        await asyncio.gather(jadx_task, apktool_task, return_exceptions=True)

        manifest_path = self._find_manifest()
        metadata = self._parse_manifest(manifest_path) if manifest_path else None
        source_files = self._collect_source_files()
        smali_files = self._collect_smali_files()

        return {
            "metadata": metadata,
            "source_files": source_files,
            "smali_files": smali_files,
            "jadx_out": str(self.jadx_out),
            "apktool_out": str(self.apktool_out),
            "manifest_path": str(manifest_path) if manifest_path else None,
        }

    async def _run_jadx(self):
        """Decompile APK to Java source using JADX."""
        cmd = [
            settings.jadx_path,
            "--output-dir", str(self.jadx_out),
            "--no-res",           # skip resources for speed
            "--show-bad-code",
            str(self.apk_path),
        ]
        await self._run_cmd(cmd, "JADX")

    async def _run_apktool(self):
        """Decode APK resources and smali using APKTool."""
        cmd = [
            settings.apktool_path,
            "d", str(self.apk_path),
            "-o", str(self.apktool_out),
            "-f",  # force overwrite
        ]
        await self._run_cmd(cmd, "APKTool")

    async def _run_cmd(self, cmd: List[str], name: str):
        try:
            proc = await asyncio.create_subprocess_exec(
                *cmd,
                stdout=asyncio.subprocess.PIPE,
                stderr=asyncio.subprocess.PIPE,
            )
            stdout, stderr = await proc.communicate()
            if proc.returncode != 0:
                logger.warning(f"{name} exited with code {proc.returncode}: {stderr.decode()[:500]}")
            else:
                logger.info(f"{name} completed successfully")
        except FileNotFoundError:
            logger.warning(f"{name} not found at path '{cmd[0]}' — skipping decompilation step")
        except Exception as e:
            logger.error(f"{name} error: {e}")

    def _find_manifest(self) -> Optional[Path]:
        """Locate AndroidManifest.xml from either APKTool or JADX output."""
        candidates = [
            self.apktool_out / "AndroidManifest.xml",
            self.jadx_out / "resources" / "AndroidManifest.xml",
        ]
        for p in candidates:
            if p.exists():
                return p
        return None

    def _parse_manifest(self, manifest_path: Path) -> AppMetadata:
        """Extract app metadata and permissions from AndroidManifest.xml."""
        try:
            tree = ET.parse(manifest_path)
            root = tree.getroot()

            ns = {"android": "http://schemas.android.com/apk/res/android"}
            package = root.get("package", "unknown")
            version_name = root.get("{http://schemas.android.com/apk/res/android}versionName")
            version_code = root.get("{http://schemas.android.com/apk/res/android}versionCode")

            # SDK
            sdk_el = root.find("uses-sdk")
            target_sdk = int(sdk_el.get("{http://schemas.android.com/apk/res/android}targetSdkVersion", 0)) if sdk_el is not None else None
            min_sdk = int(sdk_el.get("{http://schemas.android.com/apk/res/android}minSdkVersion", 0)) if sdk_el is not None else None

            # Permissions
            permissions: List[AppPermission] = []
            for perm_el in root.findall("uses-permission"):
                name = perm_el.get("{http://schemas.android.com/apk/res/android}name", "")
                permissions.append(AppPermission(
                    name=name,
                    is_dangerous=name in DANGEROUS_PERMISSIONS,
                ))

            # Application attributes
            app_el = root.find("application")
            debuggable = app_el.get("{http://schemas.android.com/apk/res/android}debuggable", "false").lower() == "true" if app_el is not None else False
            backup = app_el.get("{http://schemas.android.com/apk/res/android}allowBackup", "true").lower() == "true" if app_el is not None else True
            cleartext = app_el.get("{http://schemas.android.com/apk/res/android}usesCleartextTraffic", "false").lower() == "true" if app_el is not None else False

            # Exported components
            def get_exported(tag: str) -> List[str]:
                result = []
                if app_el is None:
                    return result
                for el in app_el.findall(tag):
                    exported = el.get("{http://schemas.android.com/apk/res/android}exported", "false").lower()
                    if exported == "true":
                        result.append(el.get("{http://schemas.android.com/apk/res/android}name", "unknown"))
                return result

            return AppMetadata(
                app_name=package.split(".")[-1],
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
            logger.error(f"Manifest parse error: {e}")
            return AppMetadata(app_name="unknown")

    def _collect_source_files(self) -> List[str]:
        """Gather all .java and .kt files from JADX output."""
        files = []
        if self.jadx_out.exists():
            for ext in ("*.java", "*.kt"):
                files.extend([str(p) for p in self.jadx_out.rglob(ext)])
        logger.info(f"Found {len(files)} source files")
        return files

    def _collect_smali_files(self) -> List[str]:
        """Gather all .smali files from APKTool output."""
        files = []
        if self.apktool_out.exists():
            files.extend([str(p) for p in self.apktool_out.rglob("*.smali")])
        logger.info(f"Found {len(files)} smali files")
        return files
