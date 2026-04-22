import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8000/api/v1',
  timeout: 300_000,
})

export interface ScanResponse {
  scan_id: string
  status: 'pending' | 'processing' | 'completed' | 'failed'
  message: string
}

export interface VulnSummary {
  critical: number
  high: number
  medium: number
  low: number
  info: number
  total: number
}

export interface RiskScore {
  overall: string
  score: number
  exploitability: number
  impact: number
  business_risk: number
  rationale: string
}

export interface CodeFix {
  language: string
  vulnerable_code?: string
  fixed_code: string
  explanation: string
}

export interface Vulnerability {
  id: string
  title: string
  severity: 'Critical' | 'High' | 'Medium' | 'Low' | 'Info'
  owasp_category: string
  cwe_id?: string
  description: string
  evidence: string
  file_path?: string
  line_number?: number
  attack_vector?: string
  impact?: string
  fix: string
  code_fix?: CodeFix
  sdlc_phase?: string
}

export interface ThreatScenario {
  threat_actor: string
  attack_vector: string
  scenario: string
  impact: string
  likelihood: string
}

export interface ThreatModel {
  attack_surface: string[]
  scenarios: ThreatScenario[]
  data_exposure_risks: string[]
}

export interface AppMetadata {
  app_name: string
  package_name?: string
  version_name?: string
  target_sdk?: number
  min_sdk?: number
  debuggable: boolean
  backup_enabled: boolean
  uses_cleartext_traffic: boolean
  permissions: { name: string; is_dangerous: boolean }[]
  exported_activities: string[]
  exported_services: string[]
}

export interface SecurityReport {
  scan_id: string
  app_name: string
  input_type: string
  status: string
  created_at: string
  completed_at?: string
  metadata?: AppMetadata
  risk_score: RiskScore
  summary: string
  vulnerabilities: Vulnerability[]
  vuln_summary: VulnSummary
  threat_model?: ThreatModel
  developer_checklist?: { items: string[] }
  owasp_compliance: Record<string, 'PASS' | 'FAIL'>
}

export interface ScanStatus {
  scan_id: string
  status: 'pending' | 'processing' | 'completed' | 'failed'
  app_name?: string
  error?: string
  current_step?: string
  report?: SecurityReport
}

// ── API functions ────────────────────────────────────────────────
export const uploadScan = async (file: File, appName: string, inputType: string): Promise<ScanResponse> => {
  const form = new FormData()
  form.append('file', file)
  form.append('app_name', appName)
  form.append('input_type', inputType)
  const { data } = await api.post<ScanResponse>('/scans', form, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
  return data
}

export const getScanStatus = async (scanId: string): Promise<ScanStatus> => {
  const { data } = await api.get<ScanStatus>(`/scans/${scanId}`)
  return data
}

export const getReport = async (scanId: string): Promise<SecurityReport> => {
  const { data } = await api.get<SecurityReport>(`/reports/${scanId}`)
  return data
}

export const listScans = async () => {
  const { data } = await api.get('/scans')
  return data
}

export default api
