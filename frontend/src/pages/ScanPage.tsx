import { useEffect, useState, useRef } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { getScanStatus, ScanStatus } from '../api/client'
import { Shield, CheckCircle, XCircle, Loader } from 'lucide-react'
import styles from './ScanPage.module.css'

const STEPS = [
  'Ingestion & Decompilation',
  'Static Analysis (SAST)',
  'Security Knowledge (RAG)',
  'Threat Modeling',
  'Risk Scoring',
  'Remediation',
  'Report Generation',
]

function getStepIndex(step: string): number {
  const map: Record<string, number> = {
    init: 0,
    ingestion: 0,
    static_analysis: 1,
    knowledge: 2,
    threat_modeling: 3,
    risk_scoring: 4,
    remediation: 5,
    report_generation: 6,
  }
  return map[step] ?? 0
}

export default function ScanPage() {
  const { scanId } = useParams<{ scanId: string }>()
  const navigate = useNavigate()
  const [status, setStatus] = useState<ScanStatus | null>(null)
  const [elapsed, setElapsed] = useState(0)
  const intervalRef = useRef<ReturnType<typeof setInterval>>()

  // Timer
  useEffect(() => {
    const t = setInterval(() => setElapsed(e => e + 1), 1000)
    return () => clearInterval(t)
  }, [])

  // Poll backend
  useEffect(() => {
    if (!scanId) return

    const poll = async () => {
      try {
        const data = await getScanStatus(scanId)
        setStatus(data)
        if (data.status === 'completed') {
          clearInterval(intervalRef.current)
          setTimeout(() => navigate(`/report/${scanId}`), 1200)
        } else if (data.status === 'failed') {
          clearInterval(intervalRef.current)
        }
      } catch { /* backend not ready yet */ }
    }

    poll()
    intervalRef.current = setInterval(poll, 3000)
    return () => clearInterval(intervalRef.current)
  }, [scanId, navigate])

  const currentStepStr = status?.current_step ?? 'init'
  const currentStepIdx = getStepIndex(currentStepStr)
  const isDone = status?.status === 'completed'
  const isFailed = status?.status === 'failed'
  const progress = isDone ? 100 : Math.round(((currentStepIdx + 1) / STEPS.length) * 100)

  const fmt = (s: number) => `${Math.floor(s / 60)}:${String(s % 60).padStart(2, '0')}`

  return (
    <div className={styles.page}>
      <div className={`card ${styles.card} fade-up`}>
        {/* Header */}
        <div className={styles.header}>
          <div className={styles.scanIcon}>
            {isDone ? <CheckCircle size={28} color="var(--low)" /> :
             isFailed ? <XCircle size={28} color="var(--critical)" /> :
             <Loader size={28} className={styles.spin} />}
          </div>
          <div>
            <h1 className={styles.title}>
              {isDone ? 'Scan Complete!' : isFailed ? 'Scan Failed' : 'Scanning…'}
            </h1>
            <p className={styles.sub}>
              {status?.app_name ?? 'Analyzing your application'}
              <span className={styles.elapsed}> · {fmt(elapsed)}</span>
            </p>
          </div>
        </div>

        {/* Progress Bar */}
        <div className="progress-bar-track">
          <div className="progress-bar-fill" style={{ width: `${progress}%` }} />
        </div>
        <div className={styles.progressLabel}>{progress}% — {STEPS[Math.min(currentStepIdx, STEPS.length - 1)]}</div>

        {/* Steps */}
        <div className={styles.steps}>
          {STEPS.map((step, i) => {
            const done = isDone || i < currentStepIdx
            const active = !isDone && i === currentStepIdx
            return (
              <div key={step} className={`${styles.step} ${done ? styles.done : ''} ${active ? styles.active : ''}`}>
                <div className={styles.stepDot}>
                  {done ? <CheckCircle size={14} /> : active ? <Loader size={14} className={styles.spin} /> : <span>{i + 1}</span>}
                </div>
                <span>{step}</span>
              </div>
            )
          })}
        </div>

        {isFailed && status?.error && (
          <div className={styles.errorBox}>
            <XCircle size={15} /> {status.error}
          </div>
        )}

        {isDone && (
          <div className={styles.successBox}>
            <CheckCircle size={15} /> Report ready — redirecting…
          </div>
        )}
      </div>
    </div>
  )
}
