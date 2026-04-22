import { VulnSummary } from '../api/client'
import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, Cell } from 'recharts'
import styles from './VulnChart.module.css'

const DATA_COLORS: Record<string, string> = {
  Critical: '#ff4757',
  High: '#ff6b35',
  Medium: '#ffa502',
  Low: '#2ed573',
  Info: '#70a1ff',
}

interface Props { summary: VulnSummary }

export default function VulnChart({ summary }: Props) {
  const data = [
    { name: 'Critical', count: summary.critical },
    { name: 'High',     count: summary.high },
    { name: 'Medium',   count: summary.medium },
    { name: 'Low',      count: summary.low },
    { name: 'Info',     count: summary.info },
  ].filter(d => d.count > 0)

  return (
    <div className={`card ${styles.container}`}>
      <div className={styles.header}>
        <h3 className={styles.title}>Vulnerability Distribution</h3>
        <span className={styles.total}>{summary.total} total</span>
      </div>

      <div className={styles.counts}>
        {Object.entries(DATA_COLORS).map(([key, color]) => {
          const val = summary[key.toLowerCase() as keyof VulnSummary] as number
          if (!val) return null
          return (
            <div key={key} className={styles.countItem}>
              <span className={styles.countNum} style={{ color }}>{val}</span>
              <span className={styles.countLabel}>{key}</span>
            </div>
          )
        })}
      </div>

      <ResponsiveContainer width="100%" height={160}>
        <BarChart data={data} margin={{ top: 0, right: 0, left: -20, bottom: 0 }}>
          <XAxis dataKey="name" tick={{ fontSize: 11, fill: '#8892b0' }} axisLine={false} tickLine={false} />
          <YAxis tick={{ fontSize: 11, fill: '#8892b0' }} axisLine={false} tickLine={false} allowDecimals={false} />
          <Tooltip
            contentStyle={{ background: '#131b2e', border: '1px solid rgba(99,120,180,0.2)', borderRadius: 8, fontSize: 12 }}
            cursor={{ fill: 'rgba(91,141,238,0.08)' }}
          />
          <Bar dataKey="count" radius={[4, 4, 0, 0]}>
            {data.map(entry => (
              <Cell key={entry.name} fill={DATA_COLORS[entry.name]} />
            ))}
          </Bar>
        </BarChart>
      </ResponsiveContainer>
    </div>
  )
}
