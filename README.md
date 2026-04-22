# BeSecure - AI-Powered Android Security Analysis System

<p align="center">
  <img width="960" height="455" alt="image" src="https://github.com/user-attachments/assets/3cdfd9b3-eecd-442a-a8c4-2c9e1f398957" />

</p>

A production-grade AI security intelligence engine that combines **static analysis tools** (JADX, APKTool, MobSF) with a **multi-agent LangGraph pipeline** and **Ollama LLMs** to automatically audit Android APKs and source code against OWASP Mobile Top 10, Secure SDLC, and ISO 27001.

---

## 🏗️ Architecture

```
Upload APK/ZIP
     ↓
FastAPI Gateway (async)
     ↓
LangGraph Pipeline ──────────────────────────────────
  1. Ingestion Agent    → JADX + APKTool decompilation
  2. Static Analysis    → 14-rule SAST + MobSF (optional)
  3. Knowledge Agent    → ChromaDB RAG (OWASP + SDLC docs)
  4. Threat Modeling    → LLM-generated threat scenarios
  5. Risk Scoring       → CVSS-style 0–10 scoring
  6. Remediation        → LLM code-level fixes
  7. Report Generator   → JSON report + OWASP matrix
     ↓
React Dashboard (Vite + TypeScript)
```

---

## 🚀 Quick Start (Local Development)

### 1. Prerequisites
- Python 3.11+
- Node 18+
- [uv](https://docs.astral.sh/uv/)
- [Ollama](https://ollama.ai) running locally
- JADX and APKTool in your PATH (for APK analysis)

### 2. Install Ollama models
```bash
ollama pull mistral
ollama pull deepseek-coder:6.7b
ollama pull llama3
```

### 3. Backend Setup
```bash
# Copy env
cp .env.example .env

# Install deps
uv sync

# Run backend
uv run python -m backend.main
```

### 4. Frontend Setup
```bash
cd frontend
npm install
npm run dev
```

Frontend → http://localhost:5173  
Backend API → http://localhost:8000  
API Docs → http://localhost:8000/docs  

### 5. Docker (All services)
```bash
docker-compose up
```

---

## 🤖 LLM Model Strategy

| Task                | Model                  |
|---------------------|------------------------|
| Code analysis       | `deepseek-coder:6.7b`  |
| Security reasoning  | `llama3` (8b/70b)      |
| Fast parsing/RAG    | `mistral:7b`           |

---

## 🔐 Security Rules Covered

- Hardcoded secrets / API keys (CWE-798)
- Insecure SharedPreferences (CWE-312)
- Weak crypto: MD5, SHA-1, DES, RC4, ECB (CWE-327)
- WebView JS/file access (CWE-749)
- SSL validation bypass (CWE-295)
- SQL injection via rawQuery (CWE-89)
- External storage of sensitive data (CWE-200)
- Logging sensitive data (CWE-532)
- Debuggable build flag (CWE-489)
- Dangerous permission analysis
- Exported component detection
- Cleartext traffic (CWE-319)

---

## 📊 Report Output

```json
{
  "app_name": "com.example.app",
  "risk_score": { "overall": "High", "score": 7.4 },
  "vuln_summary": { "critical": 2, "high": 5, "medium": 3 },
  "vulnerabilities": [...],
  "owasp_compliance": { "M1 - Improper Credential Usage": "FAIL", ... },
  "threat_model": { "scenarios": [...] },
  "developer_checklist": { "items": [...] }
}
```

---

## 📁 Project Structure

```
BeSecure/
├── backend/
│   ├── main.py                  # FastAPI entry
│   ├── config.py                # Settings
│   ├── models/schema.py         # Pydantic models
│   ├── api/routes/              # scan, report, health
│   ├── agents/
│   │   ├── state.py             # LangGraph state
│   │   ├── workflow.py          # Graph definition
│   │   └── nodes/               # 7 agent nodes
│   ├── services/                # decompiler, SAST, MobSF, files
│   ├── rag/                     # ChromaDB + seeding
│   └── llm/client.py            # Ollama model selector
├── frontend/
│   └── src/
│       ├── api/client.ts        # Typed API client
│       ├── components/          # Navbar, VulnCard, charts
│       └── pages/               # Upload, Scan progress, Report
├── docker-compose.yml
└── .env.example
```
