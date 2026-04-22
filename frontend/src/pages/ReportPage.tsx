import { useEffect, useState } from 'react'
import { useParams, useNavigate, Link } from 'react-router-dom'
import { getReport, SecurityReport, Vulnerability } from '../api/client'
import VulnCard from '../components/VulnCard'
import RiskGauge from '../components/RiskGauge'
import VulnChart from '../components/VulnChart'
import {
  Shield, ArrowLeft, CheckCircle, XCircle, ChevronRight,
  Package, Target, AlertTriangle, ClipboardList, Filter
} from 'lucide-react'
import styles from './ReportPage.module.css'

const SEVERITY_ORDER = ['Critical', 'High', 'Medium', 'Low', 'Info']

export default function ReportPage() {
  const { scanId } = useParams<{ scanId: string }>()
  const navigate = useNavigate()
  const [report, setReport] = useState<SecurityReport | null>(null)
  const [loading, setLoading] = useState(true)
  const [filter, setFilter] = useState<string>('All')
  const [activeTab, setActiveTab] = useState<'vulns' | 'threats' | 'owasp' | 'checklist'>('vulns')

  useEffect(() => {
    if (!scanId) return
    getReport(scanId)
      .then(setReport)
      .catch(() => navigate('/'))
      .finally(() => setLoading(false))
  }, [scanId, navigate])

  if (loading) return (
    <div className={styles.loading}>
      <div className={styles.loadingSpinner} />
      <span>Loading report…</span>
    </div>
  )
  if (!report) return null

  const filteredVulns: Vulnerability[] = (filter === 'All'
    ? report.vulnerabilities
    : report.vulnerabilities.filter(v => v.severity === filter)
  ).sort((a, b) =>
    SEVERITY_ORDER.indexOf(a.severity) - SEVERITY_ORDER.indexOf(b.severity)
  )

  const owaspEntries = Object.entries(report.owasp_compliance)

  return (
    <div className={styles.page}>
      {/* Top Bar */}
      <div className={styles.topBar}>
        <button className={`btn btn-outline ${styles.backBtn}`} onClick={() => navigate('/')}>
          <ArrowLeft size={15} /> New Scan
        </button>
        <h1 className={styles.appName}>
          <Package size={18} style={{ color: 'var(--accent)' }} />
          {report.app_name}
        </h1>
        <span className={styles.scanId}>ID: {scanId?.slice(0, 8)}</span>
      </div>

      {/* Summary Strip */}
      <div className={styles.strip}>
        {report.metadata?.package_name && (
          <div className={styles.stripItem}>
            <span className={styles.stripLabel}>Package</span>
            <span className={styles.stripVal}>{report.metadata.package_name}</span>
          </div>
        )}
        <div className={styles.stripItem}>
          <span className={styles.stripLabel}>Target SDK</span>
          <span className={styles.stripVal}>{report.metadata?.target_sdk ?? '—'}</span>
        </div>
        <div className={styles.stripItem}>
          <span className={styles.stripLabel}>Debuggable</span>
          <span className={report.metadata?.debuggable ? styles.danger : styles.safe}>
            {report.metadata?.debuggable ? 'YES' : 'NO'}
          </span>
        </div>
        <div className={styles.stripItem}>
          <span className={styles.stripLabel}>Cleartext HTTP</span>
          <span className={report.metadata?.uses_cleartext_traffic ? styles.danger : styles.safe}>
            {report.metadata?.uses_cleartext_traffic ? 'YES' : 'NO'}
          </span>
        </div>
        <div className={styles.stripItem}>
          <span className={styles.stripLabel}>Dangerous Perms</span>
          <span className={styles.stripVal}>
            {report.metadata?.permissions.filter(p => p.is_dangerous).length ?? 0}
          </span>
        </div>
      </div>

      {/* Main Grid */}
      <div className={styles.grid}>
        {/* Left Column */}
        <div className={styles.left}>
          <RiskGauge riskScore={report.risk_score} />
          <VulnChart summary={report.vuln_summary} />

          {/* Executive Summary */}
          <div className={`card ${styles.summaryCard}`}>
            <h3 className={styles.sectionTitle}><Shield size={15} /> Executive Summary</h3>
            <p>{report.summary}</p>
          </div>
        </div>

        {/* Right Column — Tabbed */}
        <div className={styles.right}>
          <div className={`card ${styles.tabsCard}`}>
            <div className={styles.tabs}>
              {[
                { key: 'vulns',    label: `Vulnerabilities (${report.vuln_summary.total})`, icon: AlertTriangle },
                { key: 'threats',  label: 'Threat Model', icon: Target },
                { key: 'owasp',    label: 'OWASP Compliance', icon: Shield },
                { key: 'checklist',label: 'Dev Checklist', icon: ClipboardList },
              ].map(({ key, label, icon: Icon }) => (
                <button
                  key={key}
                  className={`${styles.tab} ${activeTab === key ? styles.tabActive : ''}`}
                  onClick={() => setActiveTab(key as any)}
                >
                  <Icon size={14} /> {label}
                </button>
              ))}
            </div>

            {/* Vulnerabilities Tab */}
            {activeTab === 'vulns' && (
              <div className={styles.tabContent}>
                {/* Severity Filter */}
                <div className={styles.filterRow}>
                  <Filter size={13} />
                  {['All', ...SEVERITY_ORDER.filter(s => s !== 'Info')].map(s => (
                    <button
                      key={s}
                      className={`${styles.filterBtn} ${filter === s ? styles.filterActive : ''}`}
                      onClick={() => setFilter(s)}
                    >
                      {s}
                    </button>
                  ))}
                </div>
                <div className={styles.vulnList}>
                  {filteredVulns.length === 0
                    ? <p className={styles.empty}>No findings for this filter.</p>
                    : filteredVulns.map((v, i) => <VulnCard key={v.id} vuln={v} index={i} />)
                  }
                </div>
              </div>
            )}

            {/* Threat Model Tab */}
            {activeTab === 'threats' && (
              <div className={styles.tabContent}>
                {report.threat_model ? (
                  <>
                    <div className={styles.sectionBlock}>
                      <h4 className={styles.blockTitle}>Attack Surface</h4>
                      <ul className={styles.list}>
                        {report.threat_model.attack_surface.map((s, i) => (
                          <li key={i}><ChevronRight size={12} />{s}</li>
                        ))}
                      </ul>
                    </div>
                    <div className={styles.sectionBlock}>
                      <h4 className={styles.blockTitle}>Threat Scenarios</h4>
                      {report.threat_model.scenarios.map((sc, i) => (
                        <div key={i} className={`card ${styles.scenarioCard}`}>
                          <div className={styles.scenarioHeader}>
                            <span className={`badge badge-${sc.likelihood.toLowerCase()}`}>{sc.likelihood}</span>
                            <span className={styles.scenarioActor}>{sc.threat_actor}</span>
                            <span className={styles.scenarioVector}>{sc.attack_vector}</span>
                          </div>
                          <p className={styles.scenarioText}>{sc.scenario}</p>
                          <p className={styles.scenarioImpact}><strong>Impact:</strong> {sc.impact}</p>
                        </div>
                      ))}
                    </div>
                    <div className={styles.sectionBlock}>
                      <h4 className={styles.blockTitle}>Data Exposure Risks</h4>
                      <ul className={styles.list}>
                        {report.threat_model.data_exposure_risks.map((r, i) => (
                          <li key={i}><ChevronRight size={12} />{r}</li>
                        ))}
                      </ul>
                    </div>
                  </>
                ) : <p className={styles.empty}>No threat model generated.</p>}
              </div>
            )}

            {/* OWASP Compliance Tab */}
            {activeTab === 'owasp' && (
              <div className={styles.tabContent}>
                <div className={styles.owaspGrid}>
                  {owaspEntries.map(([cat, status]) => (
                    <div key={cat} className={`${styles.owaspItem} ${status === 'FAIL' ? styles.owaspFail : styles.owaspPass}`}>
                      {status === 'PASS'
                        ? <CheckCircle size={14} className={styles.passIcon} />
                        : <XCircle size={14} className={styles.failIcon} />
                      }
                      <span className={styles.owaspCat}>{cat}</span>
                      <span className={`badge ${status === 'PASS' ? 'badge-low' : 'badge-critical'}`}>{status}</span>
                    </div>
                  ))}
                </div>
              </div>
            )}

            {/* Developer Checklist Tab */}
            {activeTab === 'checklist' && (
              <div className={styles.tabContent}>
                <div className={styles.checklist}>
                  {(report.developer_checklist?.items ?? []).map((item, i) => {
                    const sev = item.match(/^\[(\w+)\]/)?.[1] ?? 'Info'
                    const text = item.replace(/^\[\w+\]\s*/, '')
                    return (
                      <div key={i} className={styles.checkItem}>
                        <span className={`badge badge-${sev.toLowerCase()}`}>{sev}</span>
                        <span className={styles.checkText}>{text}</span>
                      </div>
                    )
                  })}
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  )
}
