import { Link } from 'react-router-dom'

export function PageHeader({ title, children, actions }) {
  return (
    <header className="page-header">
      <div>
        <h1>{title}</h1>
        {children && <p className="lede">{children}</p>}
      </div>
      {actions && <div className="page-actions">{actions}</div>}
    </header>
  )
}

export function Loading({ label = 'Loading' }) {
  return (
    <div className="state" role="status">
      <span className="spinner" aria-hidden="true" />
      {label}
    </div>
  )
}

export function ErrorBox({ error, onRetry }) {
  if (!error) return null
  return (
    <div className="notice notice-error" role="alert">
      <div>
        <strong>Something went wrong.</strong> {error.message}
      </div>
      {onRetry && (
        <button className="btn btn-small" onClick={onRetry}>
          Try again
        </button>
      )}
    </div>
  )
}

export function Empty({ title, children, action }) {
  return (
    <div className="empty">
      <h3>{title}</h3>
      {children && <p>{children}</p>}
      {action}
    </div>
  )
}

export function Badge({ tone = 'neutral', children }) {
  return <span className={`badge badge-${tone}`}>{children}</span>
}

export function statusTone(status) {
  const s = String(status || '').toUpperCase()
  if (s === 'OPTIMAL' || s === 'OK' || s === 'FOUND' || s === 'MAPPED') return 'ok'
  if (s === 'FEASIBLE') return 'warn'
  if (s === 'INFEASIBLE' || s === 'MODEL_INVALID' || s === 'MISSING' || s === 'UNKNOWN') return 'bad'
  return 'neutral'
}

export function Panel({ title, note, children, actions }) {
  return (
    <section className="panel">
      {(title || actions) && (
        <div className="panel-head">
          <div>
            {title && <h2>{title}</h2>}
            {note && <p className="muted">{note}</p>}
          </div>
          {actions}
        </div>
      )}
      {children}
    </section>
  )
}

export function Table({ columns, rows, empty = 'Nothing to show.', rowKey }) {
  if (!rows || rows.length === 0) return <p className="muted pad">{empty}</p>
  return (
    <div className="table-wrap">
      <table>
        <thead>
          <tr>
            {columns.map((c) => (
              <th key={c.key} className={c.numeric ? 'num' : ''}>
                {c.label}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {rows.map((row, i) => (
            <tr key={rowKey ? rowKey(row, i) : i}>
              {columns.map((c) => (
                <td key={c.key} className={c.numeric ? 'num' : ''}>
                  {c.render ? c.render(row) : row[c.key]}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

export function KeyValues({ data }) {
  const entries = Object.entries(data || {}).filter(([, v]) => v === null || typeof v !== 'object')
  if (entries.length === 0) return <p className="muted">No details returned.</p>
  return (
    <dl className="kv">
      {entries.map(([k, v]) => (
        <div key={k}>
          <dt>{k.replace(/([A-Z])/g, ' $1').replace(/^./, (c) => c.toUpperCase())}</dt>
          <dd>{typeof v === 'boolean' ? (v ? 'Yes' : 'No') : String(v)}</dd>
        </div>
      ))}
    </dl>
  )
}

export function TextList({ items, tone }) {
  if (!items || items.length === 0) return null
  return (
    <ul className={`text-list ${tone || ''}`}>
      {items.map((item, i) => (
        <li key={i}>{typeof item === 'string' ? item : JSON.stringify(item)}</li>
      ))}
    </ul>
  )
}

export function LinkButton({ to, children, primary }) {
  return (
    <Link to={to} className={`btn ${primary ? 'btn-primary' : ''}`}>
      {children}
    </Link>
  )
}
