import { useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../api.js'
import { useAsync } from '../hooks.js'
import { Badge, ErrorBox, Loading, PageHeader, Panel, statusTone, Table } from '../components/ui.jsx'
import { formatDateTime } from '../utils.js'

export default function History() {
  const [year, setYear] = useState('')
  const [applied, setApplied] = useState('')
  const [busy, setBusy] = useState(false)
  const [message, setMessage] = useState(null)
  const [form, setForm] = useState({ academicYear: '2026-2027', status: 'OPTIMAL' })

  const list = useAsync(() => (applied ? api.historyByYear(applied) : api.history()), [applied])
  const counts = useAsync(async () => {
    const all = await api.history()
    const pairs = await Promise.all(all.map((h) => api.entryCount(h.id).then((n) => [h.id, n]).catch(() => [h.id, '-'])))
    return Object.fromEntries(pairs)
  }, [list.data])

  async function remove(row) {
    if (!window.confirm(`Delete timetable ${row.id}? Its lessons are removed too.`)) return
    setBusy(true)
    setMessage(null)
    try {
      await api.deleteHistory(row.id)
      setMessage({ tone: 'ok', text: `Timetable ${row.id} deleted.` })
      list.reload()
    } catch (e) {
      setMessage({ tone: 'error', text: e.message })
    } finally {
      setBusy(false)
    }
  }

  async function save(e) {
    e.preventDefault()
    setBusy(true)
    setMessage(null)
    try {
      const saved = await api.saveHistory(form.academicYear, form.status)
      setMessage({ tone: 'ok', text: `Record ${saved.id} saved. It has no lessons until you add entries.` })
      list.reload()
    } catch (err) {
      setMessage({ tone: 'error', text: err.message })
    } finally {
      setBusy(false)
    }
  }

  const rows = list.data ? [...list.data].sort((a, b) => b.id - a.id) : []

  return (
    <>
      <PageHeader title="History">Every generated timetable is kept here. Open one to review it or delete the ones you no longer need.</PageHeader>

      {message && (
        <div className={`notice ${message.tone === 'ok' ? 'notice-ok' : 'notice-error'}`} role="status">
          {message.text}
        </div>
      )}

      <Panel
        title="Saved timetables"
        actions={
          <form
            className="inline-form"
            onSubmit={(e) => {
              e.preventDefault()
              setApplied(year.trim())
            }}
          >
            <label>
              Academic year
              <input value={year} onChange={(e) => setYear(e.target.value)} placeholder="2026-2027" />
            </label>
            <button className="btn btn-small">Filter</button>
            {applied && (
              <button
                type="button"
                className="btn btn-small"
                onClick={() => {
                  setYear('')
                  setApplied('')
                }}
              >
                Clear
              </button>
            )}
          </form>
        }
      >
        <ErrorBox error={list.error} onRetry={list.reload} />
        {list.loading ? (
          <Loading />
        ) : (
          <Table
            rows={rows}
            empty="No timetables saved yet."
            rowKey={(r) => r.id}
            columns={[
              { key: 'id', label: 'ID', numeric: true },
              { key: 'academicYear', label: 'Academic year' },
              { key: 'status', label: 'Status', render: (r) => <Badge tone={statusTone(r.status)}>{r.status}</Badge> },
              { key: 'generatedAt', label: 'Generated', render: (r) => formatDateTime(r.generatedAt) },
              { key: 'lessons', label: 'Lessons', numeric: true, render: (r) => counts.data?.[r.id] ?? '' },
              {
                key: 'actions',
                label: '',
                render: (r) => (
                  <span className="row-actions">
                    <Link className="btn btn-small" to={`/timetable?id=${r.id}`}>
                      Open
                    </Link>
                    <button className="btn btn-small btn-danger" disabled={busy} onClick={() => remove(r)}>
                      Delete
                    </button>
                  </span>
                ),
              },
            ]}
          />
        )}
      </Panel>

      <Panel title="Add a record by hand" note="Creates an empty history record. Use it only for testing or imports.">
        <form className="inline-form" onSubmit={save}>
          <label>
            Academic year
            <input value={form.academicYear} onChange={(e) => setForm({ ...form, academicYear: e.target.value })} required />
          </label>
          <label>
            Status
            <select value={form.status} onChange={(e) => setForm({ ...form, status: e.target.value })}>
              <option>OPTIMAL</option>
              <option>FEASIBLE</option>
              <option>INFEASIBLE</option>
            </select>
          </label>
          <button className="btn btn-primary" disabled={busy}>
            Save record
          </button>
        </form>
      </Panel>
    </>
  )
}
