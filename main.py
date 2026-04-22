"""
BeSecure root entry point.
Run with: uv run python main.py
Or:        uv run uvicorn backend.main:app --reload
"""
from backend.main import app  # noqa: F401

if __name__ == "__main__":
    import uvicorn
    from backend.config import settings
    uvicorn.run(
        "backend.main:app",
        host=settings.app_host,
        port=settings.app_port,
        reload=True,
        log_level="info",
    )
