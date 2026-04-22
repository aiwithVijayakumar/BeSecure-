from pathlib import Path
from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    model_config = SettingsConfigDict(
        env_file=".env",
        env_file_encoding="utf-8",
        extra="ignore",
    )

    # Application
    app_env: str = "development"
    app_host: str = "0.0.0.0"
    app_port: int = 8000
    secret_key: str = "change-me-in-production"

    # File Storage
    upload_dir: Path = Path("./uploads")
    reports_dir: Path = Path("./reports")
    max_upload_size_mb: int = 100

    # Ollama
    ollama_base_url: str = "http://localhost:11434"
    code_analysis_model: str = "glm-5.1:cloud"
    reasoning_model: str = "glm-5.1:cloud"
    fast_model: str = "glm-5.1:cloud"

    # ChromaDB
    chroma_host: str = "localhost"
    chroma_port: int = 8001
    chroma_collection_owasp: str = "owasp_knowledge"
    chroma_collection_sdlc: str = "sdlc_knowledge"

    # Redis / Celery
    redis_url: str = "redis://localhost:6379/0"
    celery_broker_url: str = "redis://localhost:6379/0"
    celery_result_backend: str = "redis://localhost:6379/1"

    # MobSF
    mobsf_url: str = "http://localhost:8002"
    mobsf_api_key: str = ""

    # Static Analysis Tools
    jadx_path: str = "jadx"
    apktool_path: str = "apktool"

    def ensure_dirs(self):
        self.upload_dir.mkdir(parents=True, exist_ok=True)
        self.reports_dir.mkdir(parents=True, exist_ok=True)


settings = Settings()
