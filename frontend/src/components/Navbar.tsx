import { Link, useLocation } from 'react-router-dom'
import { Shield, Activity, List, Github } from 'lucide-react'
import styles from './Navbar.module.css'

export default function Navbar() {
  const { pathname } = useLocation()

  const links = [
    { to: '/',        label: 'New Scan',    icon: Shield },
    { to: '/scans',   label: 'All Scans',   icon: List },
  ]

  return (
    <nav className={styles.nav}>
      <Link to="/" className={styles.brand}>
        <div className={styles.brandIcon}>
          <Shield size={20} />
        </div>
        <span>Be<strong>Secure</strong></span>
      </Link>

      <div className={styles.links}>
        {links.map(({ to, label, icon: Icon }) => (
          <Link
            key={to}
            to={to}
            className={`${styles.link} ${pathname === to ? styles.active : ''}`}
          >
            <Icon size={15} />
            {label}
          </Link>
        ))}
      </div>

      <a
        href="https://owasp.org/www-project-mobile-top-10/"
        target="_blank"
        rel="noreferrer"
        className={styles.ext}
      >
        <Activity size={14} />
        OWASP Mobile Top 10
      </a>
    </nav>
  )
}
