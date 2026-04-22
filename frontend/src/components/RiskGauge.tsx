import { RiskScore as RS } from '../api/client'
import { RadialBarChart, RadialBar, ResponsiveContainer } from 'recharts'
import styles from './RiskGauge.module.css'

const COLOR_MAP: Record<string, string> = {
  Critical: '#ff4757',
  High: '#ff6b35',
  Medium: '#ffa502',
  Low: '#2ed573',
  Info: '#70a1ff',
}

interface Props { riskScore: RS }

export default function RiskGauge({ riskScore }: Props) {
  const color = COLOR_MAP[riskScore.overall] ?? '#70a1ff'
  const pct = (riskScore.score / 10) * 100

  const data = [
    { name: 'score', value: pct, fill: color },
  ]

  return (
    <div className={`card ${styles.container}`}>
      <h3 className={styles.title}>Risk Score</h3>
      <div className={styles.gauge}>
        <ResponsiveContainer width="100%" height={200}>
          <RadialBarChart
            cx="50%"
            cy="60%"
            innerRadius="70%"
            outerRadius="90%"
            startAngle={180}
            endAngle={0}
            data={data}
          >
            <RadialBar dataKey="value" cornerRadius={8} background={{ fill: '#1a2540' }} />
          </RadialBarChart>
        </ResponsiveContainer>
        <div className={styles.overlay}>
          <span className={styles.score} style={{ color }}>{riskScore.score.toFixed(1)}</span>
          <span className={styles.outOf}>/10</span>
          <span className={styles.label} style={{ color }}>{riskScore.overall}</span>
        </div>
      </div>

      <div className={styles.breakdown}>
        {[
          { label: 'Exploitability', value: riskScore.exploitability },
          { label: 'Impact',         value: riskScore.impact },
          { label: 'Business Risk',  value: riskScore.business_risk },
        ].map(({ label, value }) => (
          <div key={label} className={styles.metric}>
            <div className={styles.metricHeader}>
              <span>{label}</span>
              <span style={{ color }}>{value.toFixed(1)}</span>
            </div>
            <div className="progress-bar-track">
              <div className="progress-bar-fill" style={{ width: `${value * 10}%`, background: color }} />
            </div>
          </div>
        ))}
      </div>

      <p className={styles.rationale}>{riskScore.rationale}</p>
    </div>
  )
}
