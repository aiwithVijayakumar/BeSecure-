import { useState, useCallback } from 'react'
import { useNavigate } from 'react-router-dom'
import { useDropzone } from 'react-dropzone'
import { uploadScan } from '../api/client'
import { Upload, Shield, Zap, Lock, FileCode, AlertCircle } from 'lucide-react'
import styles from './UploadPage.module.css'

const FEATURES = [
  { icon: Shield,   title: 'OWASP Mobile Top 10',    desc: 'Full mapping to M1–M10 categories' },
  { icon: Zap,      title: 'AI-Powered Analysis',     desc: '7-agent LangGraph pipeline' },
  { icon: Lock,     title: 'Secure SDLC Validation',  desc: 'ISO 27001 & SDLC alignment' },
  { icon: FileCode, title: 'Code-Level Fixes',         desc: 'LLM-generated secure code patches' },
]

export default function UploadPage() {
  const navigate = useNavigate()
  const [file, setFile] = useState<File | null>(null)
  const [appName, setAppName] = useState('')
  const [inputType, setInputType] = useState('apk')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const onDrop = useCallback((accepted: File[]) => {
    if (accepted[0]) {
      setFile(accepted[0])
      setError('')
      if (!appName) setAppName(accepted[0].name.replace(/\.[^.]+$/, ''))
    }
  }, [appName])

  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop,
    accept: { 'application/vnd.android.package-archive': ['.apk'], 'application/zip': ['.zip'] },
    maxFiles: 1,
  })

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!file) { setError('Please select a file.'); return }
    setLoading(true)
    setError('')
    try {
      const res = await uploadScan(file, appName, inputType)
      navigate(`/scan/${res.scan_id}`)
    } catch (err: any) {
      setError(err.response?.data?.detail ?? 'Upload failed. Is the backend running?')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className={styles.page}>
      {/* Hero */}
      <div className={styles.hero}>
        <div className={styles.heroGlow} />
        <div className={styles.badge}>
          <span className="badge badge-info">AI-Powered</span>
          <span className="badge badge-low">v1.0.0</span>
        </div>
        <h1 className={styles.heroTitle}>
          Android Security<br />
          <span className={styles.gradient}>Intelligence Engine</span>
        </h1>
        <p className={styles.heroSub}>
          Upload an APK or source code and get a full OWASP Mobile Top 10 audit,
          CVSS risk scoring, threat model, and AI-generated code fixes.
        </p>
      </div>

      {/* Upload Card */}
      <div className={styles.main}>
        <form onSubmit={handleSubmit} className={`card ${styles.uploadCard}`}>
          <h2 className={styles.cardTitle}>Start New Scan</h2>

          {/* Drop Zone */}
          <div
            {...getRootProps()}
            className={`${styles.dropZone} ${isDragActive ? styles.dragActive : ''} ${file ? styles.fileReady : ''}`}
          >
            <input {...getInputProps()} />
            {file ? (
              <div className={styles.fileInfo}>
                <FileCode size={32} className={styles.fileIcon} />
                <span className={styles.fileName}>{file.name}</span>
                <span className={styles.fileSize}>{(file.size / 1024 / 1024).toFixed(2)} MB</span>
              </div>
            ) : (
              <div className={styles.dropContent}>
                <Upload size={36} className={styles.uploadIcon} />
                <span className={styles.dropTitle}>
                  {isDragActive ? 'Drop it here!' : 'Drop your APK or ZIP here'}
                </span>
                <span className={styles.dropSub}>or click to browse · max 100 MB</span>
              </div>
            )}
          </div>

          {/* App Name */}
          <div className={styles.field}>
            <label className={styles.label}>App Name</label>
            <input
              className={styles.input}
              type="text"
              placeholder="e.g. MyBankingApp"
              value={appName}
              onChange={e => setAppName(e.target.value)}
            />
          </div>

          {/* Type */}
          <div className={styles.field}>
            <label className={styles.label}>Input Type</label>
            <div className={styles.typeToggle}>
              {['apk', 'source_code'].map(t => (
                <button
                  key={t}
                  type="button"
                  className={`${styles.typeBtn} ${inputType === t ? styles.typeActive : ''}`}
                  onClick={() => setInputType(t)}
                >
                  {t === 'apk' ? 'APK File' : 'Source Code (ZIP)'}
                </button>
              ))}
            </div>
          </div>

          {error && (
            <div className={styles.error}>
              <AlertCircle size={15} /> {error}
            </div>
          )}

          <button type="submit" className={`btn btn-primary ${styles.submit}`} disabled={loading}>
            {loading
              ? <><span className={styles.spinner} /> Uploading…</>
              : <><Shield size={16} /> Start Security Scan</>
            }
          </button>
        </form>

        {/* Feature Cards */}
        <div className={styles.features}>
          {FEATURES.map(({ icon: Icon, title, desc }) => (
            <div key={title} className={`card ${styles.featureCard}`}>
              <div className={styles.featureIcon}><Icon size={18} /></div>
              <div>
                <h4 className={styles.featureTitle}>{title}</h4>
                <p className={styles.featureDesc}>{desc}</p>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}
