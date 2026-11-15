import { useEffect, useRef, useState } from 'react'
import { api } from '../api.js'
import { useAsync } from '../hooks.js'
import { Badge, ErrorBox, LinkButton, PageHeader, Panel, statusTone, TextList } from '../components/ui.jsx'

export default function Generate() {
  const [running, setRunning] = useState(false)
  const [seconds, setSeconds] = useState(0)
  const [result, setResult] = useState(null)
  const [error, setError] = useState(null)
  const [latestId, setLatestId] = useState(null)
  const timer = useRef(null)

  const pre = useAsync(async () => {
    const [validation, unassigned] = await Promise.all([api.validate().catch(() => null), api.unassigned().catch(() => null)])
    return { validation, unassigned }
  }, [])

  useEffect(() => () => clearInterval(timer.current), [])

  async function run() {
    setRunning(true)
    setResult(null)
    setError(null)
    setLatestId(null)
    setSeconds(0)
    timer.current = setInterval(() => setSeconds((s) => s + 1), 1000)
    try {
      const res = await api.generate()
      setResult(res)
      const ok = ['OPTIMAL', 'FEASIBLE'].includes(String(res.status).toUpperCase())
      if (ok) {
        const history = await api.history()
        if (history.length) setLatestId(Math.max(...history.map((h) => h.id)))
      }
    } catch (e) {
      setError(e)
    } finally {
      clearInterval(timer.current)
      setRunning(false)
    }
  }

  const unassigned = pre.data?.unassigned
  const validation = pre.data?.validation
  const warnings = []
  if (unassigned && unassigned.length > 0) warnings.push(`${unassigned.length} subjects have no teacher. Generation will stop until each has one.`)
  if (validation && !validation.valid) warnings.push(`The input data has ${validation.totalErrors} errors. Open Checks to see them.`)

  const success = result && ['OPTIMAL', 'FEASIBLE'].includes(String(result.status).toUpperCase())

  return (
    <>
      <PageHeader title="Generate timetable">
        The solver places every lesson for every section so that no teacher or section is booked twice. It usually finishes within a minute.
      </PageHeader>

      {warnings.length > 0 && (
        <div className="notice notice-warn" role="status">
          <div>
            <strong>Before you start.</strong>
            <TextList items={warnings} />
          </div>
          <LinkButton to="/checks">Open checks</LinkButton>
        </div>
      )}

      <Panel>
        <div className="run-row">
          <button className="btn btn-primary btn-large" onClick={run} disabled={running}>
            {running ? 'Generating' : 'Generate timetable'}
          </button>
          {running && (
            <p className="muted" role="status">
              <span className="spinner" aria-hidden="true" /> Solving, {seconds}s elapsed. Keep this page open.
            </p>
          )}
        </div>
      </Panel>

      <ErrorBox error={error} />

      {result && (
        <Panel title="Result">
          <div className="result-head">
            <Badge tone={statusTone(result.status)}>{result.status}</Badge>
            <span className="muted">
              Solved in {(result.solvingTimeMillis / 1000).toFixed(1)} seconds
              {Array.isArray(result.timetable) ? `, ${result.timetable.length} lessons placed` : ''}
            </span>
          </div>
          {success ? (
            <>
              <p>The timetable was saved to history.</p>
              <div className="btn-row">
                {latestId && (
                  <LinkButton primary to={`/timetable?id=${latestId}`}>
                    Open timetable
                  </LinkButton>
                )}
                <LinkButton to="/history">View history</LinkButton>
              </div>
            </>
          ) : (
            <>
              <p>
                The solver could not fit every lesson. This usually means a teacher is needed for a subject, or a teacher has too many periods.
              </p>
              <div className="btn-row">
                <LinkButton to="/checks">Run checks</LinkButton>
                <LinkButton to="/tools">Find a teacher</LinkButton>
              </div>
            </>
          )}
        </Panel>
      )}
    </>
  )
}
