import { Vulnerability } from '../api/client'
import { ChevronDown, ChevronUp, FileCode, Hash } from 'lucide-react'
import { useState } from 'react'
import styles from './VulnCard.module.css'

const SEVERITY_CLASS: Record<string, string> = {
  Critical: 'badge-critical',
  High: 'badge-high',
  Medium: 'badge-medium',
  Low: 'badge-low',
  Info: 'badge-info',
}

interface Props { vuln: Vulnerability; index: number }

export default function VulnCard({ vuln, index }: Props) {
  const [expanded, setExpanded] = useState(false)

  return (
    <div
      className={`card ${styles.card} fade-up`}
      style={{ animationDelay: `${index * 0.04}s` }}
    >
      {/* Header */}
      <div className={styles.header} onClick={() => setExpanded(e => !e)}>
        <div className={styles.left}>
          <span className={`badge ${SEVERITY_CLASS[vuln.severity] ?? 'badge-info'}`}>
            {vuln.severity}
          </span>
          <span className={styles.title}>{vuln.title}</span>
          {vuln.cwe_id && (
            <span className={styles.cwe}>
              <Hash size={11} /> {vuln.cwe_id}
            </span>
          )}
        </div>
        <div className={styles.right}>
          <span className={styles.owasp}>{vuln.owasp_category}</span>
          {expanded ? <ChevronUp size={16} /> : <ChevronDown size={16} />}
        </div>
      </div>

      {/* Expanded Body */}
      {expanded && (
        <div className={styles.body}>
          {vuln.file_path && (
            <div className={styles.location}>
              <FileCode size={13} />
              <code>{vuln.file_path.split(/[\\/]/).slice(-2).join('/')}</code>
              {vuln.line_number && <span className={styles.line}>:{vuln.line_number}</span>}
            </div>
          )}

          <p className={styles.desc}>{vuln.description}</p>

          {vuln.evidence && (
            <div className={styles.section}>
              <span className={styles.label}>Evidence</span>
              <pre className="code-block">{vuln.evidence}</pre>
            </div>
          )}

          {vuln.attack_vector && (
            <div className={styles.section}>
              <span className={styles.label}>Attack Vector</span>
              <p>{vuln.attack_vector}</p>
            </div>
          )}

          {vuln.impact && (
            <div className={styles.section}>
              <span className={styles.label}>Business Impact</span>
              <p>{vuln.impact}</p>
            </div>
          )}

          <div className={styles.fixBox}>
            <span className={styles.label}>Recommended Fix</span>
            <p>{vuln.fix}</p>
          </div>

          {vuln.code_fix && (
            <div className={styles.section}>
              <span className={styles.label}>Code Fix</span>
              {vuln.code_fix.vulnerable_code && (
                <>
                  <span className={styles.subLabel}>Vulnerable</span>
                  <pre className="code-block">
                    {vuln.code_fix.vulnerable_code.split('\n').map((l, i) => (
                      <div key={i} className="line-del">{l}</div>
                    ))}
                  </pre>
                </>
              )}
              <span className={styles.subLabel}>Fixed</span>
              <pre className="code-block">
                {vuln.code_fix.fixed_code.split('\n').map((l, i) => (
                  <div key={i} className="line-add">{l}</div>
                ))}
              </pre>
              <p className={styles.explanation}>{vuln.code_fix.explanation}</p>
            </div>
          )}

          {vuln.sdlc_phase && (
            <div className={styles.sdlc}>
              Detected in SDLC phase: <strong>{vuln.sdlc_phase}</strong>
            </div>
          )}
        </div>
      )}
    </div>
  )
}
