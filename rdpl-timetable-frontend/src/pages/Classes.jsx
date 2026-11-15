import { useState } from 'react'
import { api } from '../api.js'
import { useAsync } from '../hooks.js'
import RowEditor from '../components/RowEditor.jsx'
import { ErrorBox, Loading, PageHeader, Panel, Table } from '../components/ui.jsx'

const ITEM_COLUMNS = [
  { key: 'name', label: 'Subject or activity' },
  { key: 'type', label: 'Type', options: ['SUBJECT', 'ACTIVITY'] },
  { key: 'periodsPerWeek', label: 'Periods per week', number: true },
  { key: 'periodsPerDay', label: 'Per day', number: true },
]
const newItem = () => ({ name: '', type: 'SUBJECT', periodsPerWeek: 1, periodsPerDay: 1 })

export default function Classes() {
  const [tab, setTab] = useState('curriculum')
  const sections = useAsync(() => api.sections(), [])
  const curriculum = useAsync(() => api.curriculum(), [])
  const [openClass, setOpenClass] = useState(null)
  const [editing, setEditing] = useState(null) // { className, items }
  const [busy, setBusy] = useState(false)
  const [message, setMessage] = useState(null)
  const [newClass, setNewClass] = useState('')
  const [newSection, setNewSection] = useState({ id: '', className: '', sectionName: '', grade: 1, capacity: 35, curriculumKey: '' })

  async function run(action, success, after) {
    setBusy(true)
    setMessage(null)
    try {
      await action()
      setMessage({ tone: 'ok', text: success })
      after?.()
      return true
    } catch (e) {
      setMessage({ tone: 'error', text: e.message })
      return false
    } finally {
      setBusy(false)
    }
  }

  const saveCurriculum = async () => {
    const ok = await run(
      () => api.saveCurriculum(editing.className, { items: editing.items }),
      `Saved the curriculum for ${editing.className}.`,
      curriculum.reload,
    )
    if (ok) setEditing(null)
  }

  const removeClass = (name) => {
    if (!window.confirm(`Delete the curriculum for ${name}?`)) return
    run(() => api.deleteCurriculum(name), `Curriculum for ${name} deleted.`, curriculum.reload)
  }

  const addClass = async (e) => {
    e.preventDefault()
    const name = newClass.trim()
    const ok = await run(() => api.saveCurriculum(name, { items: [] }), `${name} added. Open it to add subjects.`, curriculum.reload)
    if (ok) {
      setNewClass('')
      setOpenClass(name)
    }
  }

  const addSection = async (e) => {
    e.preventDefault()
    const ok = await run(() => api.createSection(newSection), `Section ${newSection.sectionName} added.`, sections.reload)
    if (ok) setNewSection({ ...newSection, id: '', sectionName: '' })
  }

  const removeSection = (s) => {
    if (!window.confirm(`Delete ${s.className}, ${s.sectionName}?`)) return
    run(() => api.deleteSection(s.id), `${s.className}, ${s.sectionName} deleted.`, sections.reload)
  }

  return (
    <>
      <PageHeader title="Classes and subjects">Sections of the school and the periods each class needs every week.</PageHeader>

      {message && (
        <div className={`notice ${message.tone === 'ok' ? 'notice-ok' : 'notice-error'}`} role="status">
          {message.text}
        </div>
      )}

      <div className="segmented standalone" role="group" aria-label="Show">
        <button className={tab === 'curriculum' ? 'on' : ''} onClick={() => setTab('curriculum')}>
          Weekly curriculum
        </button>
        <button className={tab === 'sections' ? 'on' : ''} onClick={() => setTab('sections')}>
          Sections
        </button>
      </div>

      {tab === 'sections' && (
        <>
          <Panel title="Sections" note={sections.data ? `${sections.data.length} sections` : ''}>
            <ErrorBox error={sections.error} onRetry={sections.reload} />
            {sections.loading ? (
              <Loading />
            ) : (
              <Table
                rows={sections.data}
                rowKey={(r) => r.id}
                columns={[
                  { key: 'className', label: 'Class' },
                  { key: 'sectionName', label: 'Section' },
                  { key: 'id', label: 'Section ID' },
                  { key: 'capacity', label: 'Capacity', numeric: true },
                  { key: 'curriculumKey', label: 'Curriculum' },
                  {
                    key: 'actions',
                    label: '',
                    render: (r) => (
                      <button className="btn btn-small btn-danger" disabled={busy} onClick={() => removeSection(r)}>
                        Delete
                      </button>
                    ),
                  },
                ]}
              />
            )}
          </Panel>
          <Panel title="Add a section">
            <form className="inline-form" onSubmit={addSection}>
              <label>
                Section ID
                <input value={newSection.id} onChange={(e) => setNewSection({ ...newSection, id: e.target.value })} placeholder="class-5-rose" required />
              </label>
              <label>
                Class
                <input value={newSection.className} onChange={(e) => setNewSection({ ...newSection, className: e.target.value })} placeholder="Class 5" required />
              </label>
              <label>
                Section
                <input value={newSection.sectionName} onChange={(e) => setNewSection({ ...newSection, sectionName: e.target.value })} placeholder="Rose" required />
              </label>
              <label>
                Grade
                <input type="number" value={newSection.grade} onChange={(e) => setNewSection({ ...newSection, grade: Number(e.target.value) })} />
              </label>
              <label>
                Capacity
                <input type="number" value={newSection.capacity} onChange={(e) => setNewSection({ ...newSection, capacity: Number(e.target.value) })} />
              </label>
              <label>
                Curriculum key
                <input value={newSection.curriculumKey} onChange={(e) => setNewSection({ ...newSection, curriculumKey: e.target.value })} placeholder="class-5" />
              </label>
              <button className="btn btn-primary" disabled={busy}>
                Add section
              </button>
            </form>
          </Panel>
        </>
      )}

      {tab === 'curriculum' && (
        <>
          <ErrorBox error={curriculum.error} onRetry={curriculum.reload} />
          {curriculum.loading ? (
            <Loading />
          ) : (
            <>
              {(curriculum.data || []).map((c) => {
                const isOpen = openClass === c.className
                const isEditing = editing && editing.className === c.className
                return (
                  <Panel
                    key={c.className}
                    title={c.className}
                    note={`${c.totalPeriodsPerWeek} periods a week: ${c.subjectPeriodsPerWeek} subjects, ${c.activityPeriodsPerWeek} activities`}
                    actions={
                      <button
                        className="btn btn-small"
                        aria-expanded={isOpen}
                        onClick={() => {
                          setOpenClass(isOpen ? null : c.className)
                          if (isOpen) setEditing(null)
                        }}
                      >
                        {isOpen ? 'Hide' : 'Show'}
                      </button>
                    }
                  >
                    {isOpen &&
                      (isEditing ? (
                        <>
                          <RowEditor
                            rows={editing.items}
                            columns={ITEM_COLUMNS}
                            newRow={newItem}
                            addLabel="Add subject"
                            onChange={(items) => setEditing({ ...editing, items })}
                          />
                          <div className="btn-row">
                            <button className="btn btn-primary" disabled={busy} onClick={saveCurriculum}>
                              Save curriculum
                            </button>
                            <button className="btn" onClick={() => setEditing(null)}>
                              Cancel
                            </button>
                          </div>
                        </>
                      ) : (
                        <>
                          <Table
                            rows={c.items}
                            rowKey={(r, i) => `${r.name}-${i}`}
                            empty="No subjects yet."
                            columns={[
                              { key: 'name', label: 'Subject or activity' },
                              { key: 'type', label: 'Type' },
                              { key: 'periodsPerWeek', label: 'Periods per week', numeric: true },
                            ]}
                          />
                          <div className="btn-row">
                            <button className="btn" onClick={() => setEditing({ className: c.className, items: c.items.map((i) => ({ ...i })) })}>
                              Edit subjects
                            </button>
                            <button className="btn btn-danger" disabled={busy} onClick={() => removeClass(c.className)}>
                              Delete class
                            </button>
                          </div>
                        </>
                      ))}
                  </Panel>
                )
              })}
              <Panel title="Add a class">
                <form className="inline-form" onSubmit={addClass}>
                  <label>
                    Class name
                    <input value={newClass} onChange={(e) => setNewClass(e.target.value)} placeholder="Class 13" required />
                  </label>
                  <button className="btn btn-primary" disabled={busy}>
                    Add class
                  </button>
                </form>
              </Panel>
            </>
          )}
        </>
      )}
    </>
  )
}
