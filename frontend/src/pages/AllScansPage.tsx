import { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { listScans } from '../api/client'
import {
  Shield, Clock, CheckCircle, XCircle, Loader,
  ChevronRight, FileSearch, AlertTriangle, RefreshCw
} from 'lucide-react'
import styles from './AllScansPage.module.css'

interface ScanEntry {
  scan_id: string
  status: 'pending' | 'processing' | 'completed' | 'failed'
  app_name?: string
  filename?: string
}

const STATUS_CONFIG: Record<string, { icon: typeof Shield; color: string; label: string }> = {
  pending:    { icon: Clock,       color: 'var(--info)',     label: 'Pending' },
  processing: { icon: Loader,      color: 'var(--warning)',  label: 'Processing' },
  completed:  { icon: CheckCircle, color: 'var(--low)',      label: 'Completed' },
  failed:     { icon: XCircle,     color: 'var(--critical)', label: 'Failed' },
}

export default function AllScansPage() {
  const navigate = useNavigate()
  const [scans, setScans] = useState<ScanEntry[]>([])
  const [loading, setLoading] = useState(true)
  const [refreshing, setRefreshing] = useState(false)

  const fetchScans = async (isRefresh = false) => {
    if (isRefresh) setRefreshing(true)
    try {
      const data = await listScans()
      setScans(data as ScanEntry[])
    } catch { /* backend may be down */ }
    finally {
      setLoading(false)
      setRefreshing(false)
    }
  }

  useEffect(() => { fetchScans() }, [])

  const handleRowClick = (scan: ScanEntry) => {
    if (scan.status === 'completed') {
      navigate(`/report/${scan.scan_id}`)
    } else if (scan.status === 'processing' || scan.status === 'pending') {
      navigate(`/scan/${scan.scan_id}`)
    }
  }

  if (loading) return (
    <div className={styles.loading}>
      <div className={styles.loadingSpinner} />
      <span>Loading scans…</span>
    </div>
  )

  return (
    <div className={styles.page}>
      <div className={styles.header}>
        <div className={styles.headerLeft}>
          <FileSearch size={22} className={styles.headerIcon} />
          <div>
            <h1 className={styles.title}>All Scans</h1>
            <p className={styles.subtitle}>{scans.length} scan{scans.length !== 1 ? 's' : ''} total</p>
          </div>
        </div>
        <div className={styles.headerActions}>
          <button
            className={`btn btn-outline ${styles.refreshBtn}`}
            onClick={() => fetchScans(true)}
            disabled={refreshing}
          >
            <RefreshCw size={14} className={refreshing ? styles.spin : ''} />
            Refresh
          </button>
          <Link to="/" className="btn btn-primary">
            <Shield size={14} /> New Scan
          </Link>
        </div>
      </div>

      {scans.length === 0 ? (
        <div className={`card ${styles.emptyCard}`}>
          <AlertTriangle size={40} className={styles.emptyIcon} />
          <h3>No scans yet</h3>
          <p>Upload an APK or source code ZIP to start your first security scan.</p>
          <Link to="/" className="btn btn-primary">Start First Scan</Link>
        </div>
      ) : (
        <div className={styles.grid}>
          {scans.map(scan => {
            const cfg = STATUS_CONFIG[scan.status] || STATUS_CONFIG.pending
            const StatusIcon = cfg.icon
            return (
              <div
                key={scan.scan_id}
                className={`card ${styles.scanCard}`}
                onClick={() => handleRowClick(scan)}
                role="button"
                tabIndex={0}
              >
                <div className={styles.cardTop}>
                  <div className={styles.statusBadge} style={{ background: cfg.color }}>
                    <StatusIcon size={12} />
                    {cfg.label}
                  </div>
                  <span className={styles.scanIdBadge}>
                    {scan.scan_id.slice(0, 8)}…
                  </span>
                </div>
                <h3 className={styles.appName}>
                  {scan.app_name || scan.filename || 'Unknown App'}
                </h3>
                {scan.filename && (
                  <p className={styles.filename}>{scan.filename}</p>
                )}
                <div className={styles.cardAction}>
                  <span>
                    {scan.status === 'completed' ? 'View Report' :
                     scan.status === 'processing' ? 'View Progress' :
                     scan.status === 'failed' ? 'View Details' : 'Waiting…'}
                  </span>
                  <ChevronRight size={14} />
                </div>
              </div>
            )
          })}
        </div>
      )}
    </div>
  )
}
