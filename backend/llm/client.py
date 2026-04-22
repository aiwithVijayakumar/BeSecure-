"""LLM client — Ollama model selector with task-based routing."""
from functools import lru_cache
from langchain_ollama import ChatOllama
from backend.config import settings


class LLMClient:
    """
    Hybrid model selector:
      - Code analysis  → deepseek-coder
      - Reasoning      → llama3 (70b or 8b based on env)
      - Fast parsing   → mistral
    """

    def __init__(self):
        self._base = settings.ollama_base_url

    def _get(self, model: str, temperature: float = 0.1) -> ChatOllama:
        return ChatOllama(
            base_url=self._base,
            model=model,
            temperature=temperature,
        )

    def code_model(self) -> ChatOllama:
        """Use for decompiled Java/Kotlin code analysis."""
        return self._get(settings.code_analysis_model)

    def reasoning_model(self) -> ChatOllama:
        """Use for security reasoning, OWASP mapping, threat modelling."""
        return self._get(settings.reasoning_model, temperature=0.2)

    def fast_model(self) -> ChatOllama:
        """Use for quick parsing, classification, summarisation."""
        return self._get(settings.fast_model, temperature=0.0)


@lru_cache(maxsize=1)
def get_llm_client() -> LLMClient:
    return LLMClient()
