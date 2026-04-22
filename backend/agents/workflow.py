"""
LangGraph Workflow
Orchestrates the 7-agent security analysis pipeline.
"""
from typing import Callable, Optional
from langgraph.graph import StateGraph, END
from loguru import logger

from backend.agents.state import AgentState
from backend.agents.nodes.ingestion import ingestion_node
from backend.agents.nodes.static_analysis import static_analysis_node
from backend.agents.nodes.knowledge import knowledge_node
from backend.agents.nodes.threat_model import threat_model_node
from backend.agents.nodes.risk_scoring import risk_scoring_node
from backend.agents.nodes.remediation import remediation_node
from backend.agents.nodes.report_gen import report_gen_node

# Global progress callback — set by the scan route
_progress_callback: Optional[Callable[[str, str], None]] = None


def set_progress_callback(cb: Optional[Callable[[str, str], None]]):
    """Set a callback(scan_id, step_name) for progress tracking."""
    global _progress_callback
    _progress_callback = cb


def _wrap_node(node_fn, step_name: str):
    """Wrap a node function to report progress before execution."""
    async def wrapper(state: AgentState) -> AgentState:
        scan_id = str(state.get("scan_id", ""))
        if _progress_callback and scan_id:
            _progress_callback(scan_id, step_name)
        return await node_fn(state)
    wrapper.__name__ = node_fn.__name__
    return wrapper


def should_continue_after_ingestion(state: AgentState) -> str:
    """Skip analysis if ingestion completely failed."""
    if not state.get("source_files") and not state.get("smali_files"):
        if state.get("input_type") == "apk" and not state.get("manifest_path"):
            logger.warning("Ingestion produced no files — jumping to report")
            return "report"
    return "static_analysis"


def build_workflow() -> StateGraph:
    graph = StateGraph(AgentState)

    # Register nodes with progress wrapping
    graph.add_node("ingestion", _wrap_node(ingestion_node, "ingestion"))
    graph.add_node("static_analysis", _wrap_node(static_analysis_node, "static_analysis"))
    graph.add_node("knowledge", _wrap_node(knowledge_node, "knowledge"))
    graph.add_node("threat_modeling", _wrap_node(threat_model_node, "threat_modeling"))
    graph.add_node("risk_scoring", _wrap_node(risk_scoring_node, "risk_scoring"))
    graph.add_node("remediation", _wrap_node(remediation_node, "remediation"))
    graph.add_node("report", _wrap_node(report_gen_node, "report_generation"))

    # Entry point
    graph.set_entry_point("ingestion")

    # Conditional edge after ingestion
    graph.add_conditional_edges(
        "ingestion",
        should_continue_after_ingestion,
        {
            "static_analysis": "static_analysis",
            "report": "report",
        },
    )

    # Linear pipeline
    graph.add_edge("static_analysis", "knowledge")
    graph.add_edge("knowledge", "threat_modeling")
    graph.add_edge("threat_modeling", "risk_scoring")
    graph.add_edge("risk_scoring", "remediation")
    graph.add_edge("remediation", "report")
    graph.add_edge("report", END)

    return graph


# Compile once at module load
_compiled_graph = None


def get_compiled_graph():
    global _compiled_graph
    if _compiled_graph is None:
        _compiled_graph = build_workflow().compile()
        logger.info("LangGraph workflow compiled successfully")
    return _compiled_graph


async def run_scan_pipeline(initial_state: AgentState) -> AgentState:
    """Execute the full security scan pipeline."""
    graph = get_compiled_graph()
    logger.info(f"Starting scan pipeline | scan_id={initial_state.get('scan_id')}")
    final_state = await graph.ainvoke(initial_state)
    logger.info(f"Scan pipeline complete | scan_id={initial_state.get('scan_id')}")
    return final_state
