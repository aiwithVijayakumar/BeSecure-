"""
BeSecure Backend - FastAPI Entry Point
"""
import uvicorn
from contextlib import asynccontextmanager

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from fastapi.middleware.trustedhost import TrustedHostMiddleware
from loguru import logger

from backend.config import settings
from backend.api.routes import scan, report, health
from backend.rag.vectorstore import init_vectorstore


@asynccontextmanager
async def lifespan(app: FastAPI):
    """Startup and shutdown events."""
    logger.info("🚀 BeSecure API starting up...")
    settings.ensure_dirs()
    await init_vectorstore()
    logger.info("✅ Vector store initialized")
    yield
    logger.info("🛑 BeSecure API shutting down...")


app = FastAPI(
    title="BeSecure API",
    description="AI-Powered Android Application Security Analysis System",
    version="1.0.0",
    docs_url="/docs",
    redoc_url="/redoc",
    lifespan=lifespan,
)

# ─── Middleware ─────────────────────────────────────────────────────────────────
app.add_middleware(
    CORSMiddleware,
    allow_origins=[
        "http://localhost:5173",
        "http://localhost:5174",
        "http://localhost:5175",
        "http://localhost:3000",
    ],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# ─── Routers ────────────────────────────────────────────────────────────────────
app.include_router(health.router, prefix="/api/v1", tags=["Health"])
app.include_router(scan.router, prefix="/api/v1", tags=["Scan"])
app.include_router(report.router, prefix="/api/v1", tags=["Reports"])


if __name__ == "__main__":
    uvicorn.run(
        "backend.main:app",
        host=settings.app_host,
        port=settings.app_port,
        reload=settings.app_env == "development",
        log_level="info",
    )
