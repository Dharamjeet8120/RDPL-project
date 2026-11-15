import { useEffect, useMemo, useState } from 'react'
import { useSearchParams } from 'react-router-dom'
import { api } from '../api.js'
import { useAsync } from '../hooks.js'
import TimetableGrid from '../components/TimetableGrid.jsx'
import { Empty, ErrorBox, LinkButton, Loading, PageHeader, Panel } from '../components/ui.jsx'
import { formatDateTime, subjectColor } from '../utils.js'

async function loadBase() {
  const [history, sections, teachers, slots] = await Promise.all([api.history(), api.sections(), api.teachers(), api.timeSlots()])
  return { history, sections, teachers, slots }
}

export default function Timetable() {
  const [params, setParams] = useSearchParams()
  const base = useAsync(loadBase, [])
  const [mode, setMode] = useState('section')
  const [selected, setSelected] = useState('')
  const [tab, setTab] = useState('grid')

  const sortedHistory = useMemo(() => (base.data ? [...base.data.history].sort((a, b) => b.id - a.id) : []), [base.data])
  const historyId = params.get('id') || (sortedHistory[0] ? String(sortedHistory[0].id) : '')

  const entriesState = useAsync(() => (historyId ? api.entries(historyId) : Promise.resolve([])), [historyId])
  const entries = entriesState.data || []

  const sectionNames = useMemo(() => {
    const map = new Map()
    base.data?.sections.forEach((s) => map.set(s.id, { name: `${s.className}, ${s.sectionName}`, className: s.className }))
    return map
  }, [base.data])
  const teacherNames = useMemo(() => {
    const map = new Map()
    base.data?.teachers.forEach((t) => map.set(t.teacherId, t.teacherName))
    return map
  }, [base.data])

  const options = useMemo(() => {
    const ids = new Set(entries.map((e) => (mode === 'section' ? e.sectionId : e.teacherId)))
    const counts = new Map()
    entries.forEach((e) => {
      const k = mode === 'section' ? e.sectionId : e.teacherId
      counts.set(k, (counts.get(k) || 0) + 1)
    })
    const list = [...ids].filter(Boolean).map((id) => ({
      id,
      label: mode === 'section' ? sectionNames.get(id)?.name || id : teacherNames.get(id) || id,
      count: counts.get(id),
    }))
    return list.sort((a, b) => a.label.localeCompare(b.label, undefined, { numeric: true }))
  }, [entries, mode, sectionNames, teacherNames])

  useEffect(() => {
    if (options.length && !options.some((o) => o.id === selected)) setSelected(options[0].id)
  }, [options, selected])

  const shown = useMemo(
    () => entries.filter((e) => (mode === 'section' ? e.sectionId : e.teacherId) === selected),
    [entries, mode, selected],
  )

  const summary = useMemo(() => {
    const map = new Map()
    shown.forEach((e) => map.set(e.subjectName, (map.get(e.subjectName) || 0) + 1))
    return [...map.entries()].sort((a, b) => b[1] - a[1])
  }, [shown])

  const text = useAsync(
    () => (tab === 'text' && historyId && selected ? api.textById(historyId, mode, selected) : Promise.resolve('')),
    [tab, historyId, mode, selected],
  )

  const detailOf = (e) => (mode === 'section' ? teacherNames.get(e.teacherId) || e.teacherId : sectionNames.get(e.sectionId)?.name || e.sectionId)
  const title = options.find((o) => o.id === selected)?.label || ''
  const current = sortedHistory.find((h) => String(h.id) === String(historyId))

  if (base.loading) return <Loading label="Loading timetable" />
  if (base.error) return <ErrorBox error={base.error} onRetry={base.reload} />

  if (sortedHistory.length === 0) {
    return (
      <>
        <PageHeader title="Timetable" />
        <Empty
          title="No timetable yet"
          action={
            <LinkButton primary to="/generate">
              Generate timetable
            </LinkButton>
          }
        >
          Generate one first, then it shows up here.
        </Empty>
      </>
    )
  }

  return (
    <>
      <PageHeader
        title="Timetable"
        actions={
          <button className="btn" onClick={() => window.print()}>
            Print
          </button>
        }
      >
        {current ? `Timetable ${current.id}, ${current.status}, generated ${formatDateTime(current.generatedAt)}` : ''}
      </PageHeader>

      <Panel>
        <div className="toolbar">
          <label>
            Saved timetable
            <select value={historyId} onChange={(e) => setParams({ id: e.target.value })}>
              {sortedHistory.map((h) => (
                <option key={h.id} value={h.id}>
                  {h.id}, {h.status}, {formatDateTime(h.generatedAt)}
                </option>
              ))}
            </select>
          </label>
          <div className="segmented" role="group" aria-label="View by">
            <button className={mode === 'section' ? 'on' : ''} onClick={() => setMode('section')}>
              By section
            </button>
            <button className={mode === 'teacher' ? 'on' : ''} onClick={() => setMode('teacher')}>
              By teacher
            </button>
          </div>
          <label className="grow">
            {mode === 'section' ? 'Section' : 'Teacher'}
            <select value={selected} onChange={(e) => setSelected(e.target.value)}>
              {options.map((o) => (
                <option key={o.id} value={o.id}>
                  {o.label} ({o.count} periods)
                </option>
              ))}
            </select>
          </label>
          <div className="segmented" role="group" aria-label="Display">
            <button className={tab === 'grid' ? 'on' : ''} onClick={() => setTab('grid')}>
              Grid
            </button>
            <button className={tab === 'text' ? 'on' : ''} onClick={() => setTab('text')}>
              Text
            </button>
          </div>
        </div>
      </Panel>

      <ErrorBox error={entriesState.error} onRetry={entriesState.reload} />
      {entriesState.loading && <Loading label="Loading lessons" />}

      {!entriesState.loading && !entriesState.error && (
        <Panel title={title} note={`${shown.length} periods this week`}>
          {tab === 'grid' ? (
            <>
              <TimetableGrid entries={shown} slots={base.data.slots} detailOf={detailOf} />
              <ul className="legend" aria-label="Periods per subject">
                {summary.map(([name, count]) => (
                  <li key={name} style={subjectColor(name)}>
                    {name}
                    <b>{count}</b>
                  </li>
                ))}
              </ul>
            </>
          ) : (
            <>
              <ErrorBox error={text.error} onRetry={text.reload} />
              {text.loading ? (
                <Loading />
              ) : (
                <>
                  <div className="btn-row">
                    <button className="btn btn-small" onClick={() => navigator.clipboard?.writeText(text.data || '')}>
                      Copy text
                    </button>
                  </div>
                  <pre className="text-view">{text.data}</pre>
                </>
              )}
            </>
          )}
        </Panel>
      )}
    </>
  )
}
