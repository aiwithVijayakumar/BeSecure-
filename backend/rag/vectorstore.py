"""
ChromaDB Vector Store
Manages two collections:
  - owasp_knowledge   : OWASP Mobile Top 10 + Testing Guide
  - sdlc_knowledge    : Secure SDLC + Coding Guidelines
"""
import chromadb
from chromadb.config import Settings as ChromaSettings
from langchain_community.vectorstores import Chroma
from langchain_ollama import OllamaEmbeddings
from langchain_text_splitters import RecursiveCharacterTextSplitter
from langchain_community.document_loaders import PyPDFLoader, TextLoader
from pathlib import Path
from loguru import logger

from backend.config import settings

_chroma_client: chromadb.ClientAPI | None = None
_owasp_store: Chroma | None = None
_sdlc_store: Chroma | None = None

SEED_DIR = Path(__file__).parent / "seed_data"


def _get_embeddings() -> OllamaEmbeddings:
    return OllamaEmbeddings(
        base_url=settings.ollama_base_url,
        model=settings.fast_model,
    )


def _get_client() -> chromadb.ClientAPI:
    global _chroma_client
    if _chroma_client is None:
        _chroma_client = chromadb.HttpClient(
            host=settings.chroma_host,
            port=settings.chroma_port,
        )
    return _chroma_client


async def init_vectorstore():
    """
    Initialize ChromaDB. Seeds the OWASP and SDLC collections
    from local seed_data/ files if the collections are empty.
    """
    global _owasp_store, _sdlc_store

    try:
        embeddings = _get_embeddings()
        client = _get_client()

        _owasp_store = Chroma(
            client=client,
            collection_name=settings.chroma_collection_owasp,
            embedding_function=embeddings,
        )
        _sdlc_store = Chroma(
            client=client,
            collection_name=settings.chroma_collection_sdlc,
            embedding_function=embeddings,
        )

        await _seed_if_empty(_owasp_store, "owasp")
        await _seed_if_empty(_sdlc_store, "sdlc")
        logger.info("ChromaDB vector stores ready")

    except Exception as e:
        logger.warning(f"ChromaDB not available (running without RAG): {e}")


async def _seed_if_empty(store: Chroma, tag: str):
    """Load seed documents into ChromaDB if the collection is empty."""
    try:
        count = store._collection.count()
        if count > 0:
            logger.info(f"Collection '{tag}' already has {count} documents — skipping seed")
            return
    except Exception:
        pass

    seed_dir = SEED_DIR / tag
    if not seed_dir.exists():
        logger.info(f"No seed data found at {seed_dir}")
        return

    splitter = RecursiveCharacterTextSplitter(chunk_size=1000, chunk_overlap=200)
    docs = []

    for file in seed_dir.iterdir():
        try:
            if file.suffix == ".pdf":
                loader = PyPDFLoader(str(file))
            elif file.suffix in (".txt", ".md"):
                loader = TextLoader(str(file), encoding="utf-8")
            else:
                continue
            docs.extend(loader.load())
            logger.info(f"Loaded seed file: {file.name}")
        except Exception as e:
            logger.warning(f"Could not load {file.name}: {e}")

    if docs:
        chunks = splitter.split_documents(docs)
        store.add_documents(chunks)
        logger.info(f"Seeded {len(chunks)} chunks into '{tag}' collection")


def get_owasp_retriever(k: int = 5):
    if _owasp_store is None:
        return None
    return _owasp_store.as_retriever(search_kwargs={"k": k})


def get_sdlc_retriever(k: int = 5):
    if _sdlc_store is None:
        return None
    return _sdlc_store.as_retriever(search_kwargs={"k": k})
