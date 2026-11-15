import { useState } from 'react'
import { api } from '../api.js'
import { useAsync } from '../hooks.js'
import { Badge, ErrorBox, KeyValues, Loading, PageHeader, Panel, Table } from '../components/ui.jsx'

export default function Tools() {
  const curriculum = useAsync(() => api.curriculum(), [])
  const [form, setForm] = useState({ className: '', subjectName: '', type: 'SUBJECT' })
  const [teachers, setTeachers] = useState(null)
  const [findError, setFindError] = useState(null)
  const [busy, setBusy] = useState(false)

  const [test, setTest] = useState({ sectionId: 'class-1-marigold', subjectName: 'English', teacherId: 'E2904' })
  const [testResult, setTestResult] = useState(null)
  const [testError, setTestError] = useState(null)
  const [testing, setTesting] = useState(false)

  const classes = (curriculum.data || []).map((c) => c.className)
  const subjects = [
    ...new Set(
      (curriculum.data || [])
        .filter((c) => !form.className || c.className === form.className)
        .flatMap((c) => c.items.map((i) => i.name)),
    ),
  ].sort()

  async function find(e) {
    e.preventDefault()
    setBusy(true)
    setFindError(null)
    setTeachers(null)
    try {
      setTeachers(await api.eligible(form.className, form.subjectName, form.type))
    } catch (err) {
      setFindError(err)
    } finally {
      setBusy(false)
    }
  }

  async function runTest(e) {
    e.preventDefault()
    setTesting(true)
    setTestError(null)
    setTestResult(null)
    try {
      setTestResult(await api.solverTest(test.sectionId, test.subjectName, test.teacherId))
    } catch (err) {
      setTestError(err)
    } finally {
      setTesting(false)
    }
  }

  return (
    <>
      <PageHeader title="Find a teacher">See who can teach a subject in a class, and try the solver on a single lesson.</PageHeader>

      <Panel title="Eligible teachers" note="Teachers whose assignments match the class, subject and type.">
        {curriculum.loading ? (
          <Loading />
        ) : (
          <form className="inline-form" onSubmit={find}>
            <label>
              Class
              <select value={form.className} onChange={(e) => setForm({ ...form, className: e.target.value, subjectName: '' })} required>
                <option value="">Choose a class</option>
                {classes.map((c) => (
                  <option key={c}>{c}</option>
                ))}
              </select>
            </label>
            <label>
              Subject or activity
              <input list="subject-list" value={form.subjectName} onChange={(e) => setForm({ ...form, subjectName: e.target.value })} required />
              <datalist id="subject-list">
                {subjects.map((s) => (
                  <option key={s} value={s} />
                ))}
              </datalist>
            </label>
            <label>
              Type
              <select value={form.type} onChange={(e) => setForm({ ...form, type: e.target.value })}>
                <option>SUBJECT</option>
                <option>ACTIVITY</option>
              </select>
            </label>
            <button className="btn btn-primary" disabled={busy}>
              Find teachers
            </button>
          </form>
        )}
        <ErrorBox error={findError} />
        {teachers && (
          <>
            <p className="pad-top">
              {teachers.length === 0 ? (
                <>
                  <Badge tone="bad">No teacher</Badge> Nobody is assigned to this subject in this class.
                </>
              ) : (
                <>
                  <Badge tone="ok">{teachers.length}</Badge> eligible {teachers.length === 1 ? 'teacher' : 'teachers'}
                </>
              )}
            </p>
            <Table
              rows={teachers}
              rowKey={(t) => t.teacherId}
              empty=""
              columns={[
                { key: 'teacherId', label: 'ID' },
                { key: 'teacherName', label: 'Name' },
                { key: 'totalPeriodsPerWeek', label: 'Periods listed', numeric: true },
              ]}
            />
          </>
        )}
      </Panel>

      <Panel title="Solver test" note="Places one subject for one teacher across the week. Useful to confirm the solver is working.">
        <form className="inline-form" onSubmit={runTest}>
          <label>
            Section ID
            <input value={test.sectionId} onChange={(e) => setTest({ ...test, sectionId: e.target.value })} />
          </label>
          <label>
            Subject
            <input value={test.subjectName} onChange={(e) => setTest({ ...test, subjectName: e.target.value })} />
          </label>
          <label>
            Teacher ID
            <input value={test.teacherId} onChange={(e) => setTest({ ...test, teacherId: e.target.value })} />
          </label>
          <button className="btn" disabled={testing}>
            {testing ? 'Running' : 'Run test'}
          </button>
        </form>
        <ErrorBox error={testError} />
        {testResult && (
          <div className="pad-top">
            <KeyValues data={testResult} />
          </div>
        )}
      </Panel>
    </>
  )
}
