import { Fragment, useMemo, useState } from 'react'
import { api } from '../api.js'
import { useAsync } from '../hooks.js'
import RowEditor from '../components/RowEditor.jsx'
import { Badge, ErrorBox, Loading, PageHeader, Panel } from '../components/ui.jsx'

const ASSIGNMENT_COLUMNS = [
  { key: 'className', label: 'Class', list: 'class-list' },
  { key: 'subjectOrActivity', label: 'Subject or activity', list: 'subject-list' },
  { key: 'type', label: 'Type', options: ['SUBJECT', 'ACTIVITY'] },
  { key: 'periodsPerWeek', label: 'Periods per section', number: true },
]

const newAssignment = () => ({ className: 'Class 1', sectionId: null, sectionName: null, type: 'SUBJECT', subjectOrActivity: '', periodsPerWeek: 4 })

export default function Teachers() {
  const { data, loading, error, reload } = useAsync(() => api.teachers(), [])
  const curriculum = useAsync(() => api.curriculum(), [])
  const [search, setSearch] = useState('')
  const [onlyIdle, setOnlyIdle] = useState(false)
  const [open, setOpen] = useState(null)
  const [editing, setEditing] = useState(null) // { id, rows }
  const [busy, setBusy] = useState(false)
  const [message, setMessage] = useState(null)
  const [adding, setAdding] = useState(false)
  const [newTeacher, setNewTeacher] = useState({ teacherId: '', teacherName: '' })

  const rows = useMemo(() => {
    const term = search.trim().toLowerCase()
    return (data || []).filter((t) => {
      if (onlyIdle && (t.assignments || []).length > 0) return false
      if (!term) return true
      return (
        t.teacherName.toLowerCase().includes(term) ||
        String(t.teacherId).toLowerCase().includes(term) ||
        (t.assignments || []).some((a) => a.subjectOrActivity.toLowerCase().includes(term))
      )
    })
  }, [data, search, onlyIdle])

  const idle = (data || []).filter((t) => (t.assignments || []).length === 0).length
  const classNames = (curriculum.data || []).map((c) => c.className)
  const subjectNames = [...new Set((curriculum.data || []).flatMap((c) => c.items.map((i) => i.name)))].sort()

  async function run(action, success) {
    setBusy(true)
    setMessage(null)
    try {
      await action()
      setMessage({ tone: 'ok', text: success })
      reload()
      return true
    } catch (e) {
      setMessage({ tone: 'error', text: e.message })
      return false
    } finally {
      setBusy(false)
    }
  }

  const startEdit = (t) => {
    setOpen(t.teacherId)
    setEditing({ id: t.teacherId, rows: (t.assignments || []).map((a) => ({ ...a })) })
  }

  const saveEdit = async () => {
    const ok = await run(() => api.replaceAssignments(editing.id, editing.rows), `Saved assignments for ${editing.id}.`)
    if (ok) setEditing(null)
  }

  const remove = (t) => {
    if (!window.confirm(`Delete ${t.teacherName}? Their assignments are removed too.`)) return
    run(() => api.deleteTeacher(t.teacherId), `${t.teacherName} deleted.`)
  }

  const addTeacher = async (e) => {
    e.preventDefault()
    const ok = await run(
      () => api.createTeacher({ teacherId: newTeacher.teacherId.trim(), teacherName: newTeacher.teacherName.trim(), assignments: [] }),
      `${newTeacher.teacherName} added. Open them to add assignments.`,
    )
    if (ok) {
      setNewTeacher({ teacherId: '', teacherName: '' })
      setAdding(false)
    }
  }

  return (
    <>
      <PageHeader
        title="Teachers"
        actions={
          <button className="btn btn-primary" onClick={() => setAdding(!adding)}>
            {adding ? 'Cancel' : 'Add teacher'}
          </button>
        }
      >
        {data ? `${data.length} teachers, ${idle} without any assignment.` : 'Who teaches what, class by class.'}
      </PageHeader>

      {message && (
        <div className={`notice ${message.tone === 'ok' ? 'notice-ok' : 'notice-error'}`} role="status">
          {message.text}
        </div>
      )}
      <ErrorBox error={error} onRetry={reload} />

      {adding && (
        <Panel title="New teacher">
          <form className="inline-form" onSubmit={addTeacher}>
            <label>
              Teacher ID
              <input value={newTeacher.teacherId} onChange={(e) => setNewTeacher({ ...newTeacher, teacherId: e.target.value })} required />
            </label>
            <label>
              Name
              <input value={newTeacher.teacherName} onChange={(e) => setNewTeacher({ ...newTeacher, teacherName: e.target.value })} required />
            </label>
            <button className="btn btn-primary" disabled={busy}>
              Save teacher
            </button>
          </form>
        </Panel>
      )}

      <datalist id="class-list">
        {classNames.map((c) => (
          <option key={c} value={c} />
        ))}
      </datalist>
      <datalist id="subject-list">
        {subjectNames.map((s) => (
          <option key={s} value={s} />
        ))}
      </datalist>

      {loading ? (
        <Loading />
      ) : (
        <Panel>
          <div className="toolbar">
            <label className="grow">
              Search by name, ID or subject
              <input value={search} onChange={(e) => setSearch(e.target.value)} placeholder="For example Mathematics" />
            </label>
            <label className="check">
              <input type="checkbox" checked={onlyIdle} onChange={(e) => setOnlyIdle(e.target.checked)} />
              Only teachers without assignments
            </label>
          </div>
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Name</th>
                  <th className="num">Assignments</th>
                  <th className="num">Periods listed</th>
                  <th />
                </tr>
              </thead>
              <tbody>
                {rows.map((t) => {
                  const isOpen = open === t.teacherId
                  const count = (t.assignments || []).length
                  const isEditing = editing && editing.id === t.teacherId
                  return (
                    <Fragment key={t.teacherId}>
                      <tr>
                        <td>{t.teacherId}</td>
                        <td>{t.teacherName}</td>
                        <td className="num">{count || <Badge>None</Badge>}</td>
                        <td className="num">{t.totalPeriodsPerWeek}</td>
                        <td className="num">
                          <span className="row-actions">
                            <button
                              className="btn btn-small"
                              aria-expanded={isOpen}
                              onClick={() => {
                                setOpen(isOpen ? null : t.teacherId)
                                if (isOpen) setEditing(null)
                              }}
                            >
                              {isOpen ? 'Hide' : 'Open'}
                            </button>
                            <button className="btn btn-small btn-danger" disabled={busy} onClick={() => remove(t)}>
                              Delete
                            </button>
                          </span>
                        </td>
                      </tr>
                      {isOpen && (
                        <tr className="detail-row">
                          <td colSpan={5}>
                            {isEditing ? (
                              <>
                                <RowEditor
                                  rows={editing.rows}
                                  columns={ASSIGNMENT_COLUMNS}
                                  newRow={newAssignment}
                                  addLabel="Add assignment"
                                  onChange={(r) => setEditing({ ...editing, rows: r })}
                                />
                                <div className="btn-row">
                                  <button className="btn btn-primary" disabled={busy} onClick={saveEdit}>
                                    Save assignments
                                  </button>
                                  <button className="btn" onClick={() => setEditing(null)}>
                                    Cancel
                                  </button>
                                </div>
                              </>
                            ) : (
                              <>
                                {count > 0 ? (
                                  <table className="inner">
                                    <thead>
                                      <tr>
                                        <th>Class</th>
                                        <th>Subject or activity</th>
                                        <th>Type</th>
                                        <th className="num">Periods per section</th>
                                      </tr>
                                    </thead>
                                    <tbody>
                                      {t.assignments.map((a, i) => (
                                        <tr key={i}>
                                          <td>{a.className}</td>
                                          <td>{a.subjectOrActivity}</td>
                                          <td>{a.type}</td>
                                          <td className="num">{a.periodsPerWeek}</td>
                                        </tr>
                                      ))}
                                    </tbody>
                                  </table>
                                ) : (
                                  <p className="muted">No assignments yet.</p>
                                )}
                                <div className="btn-row">
                                  <button className="btn" onClick={() => startEdit(t)}>
                                    Edit assignments
                                  </button>
                                </div>
                              </>
                            )}
                          </td>
                        </tr>
                      )}
                    </Fragment>
                  )
                })}
              </tbody>
            </table>
            {rows.length === 0 && <p className="muted pad">No teachers match this search.</p>}
          </div>
        </Panel>
      )}
    </>
  )
}
