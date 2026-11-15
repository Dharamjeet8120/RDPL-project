import { useState } from 'react'
import { api } from '../api.js'
import { useAsync } from '../hooks.js'
import { Badge, ErrorBox, KeyValues, Loading, PageHeader, Panel } from '../components/ui.jsx'

export default function Database() {
  const status = useAsync(() => api.dataStatus(), [])
  const [busy, setBusy] = useState(false)
  const [message, setMessage] = useState(null)

  async function reimport() {
    const ok = window.confirm(
      'This replaces all teachers, sections and curriculum in the database with the JSON files. Any edits you made here are lost. Continue?',
    )
    if (!ok) return
    setBusy(true)
    setMessage(null)
    try {
      const result = await api.importData()
      setMessage({ tone: 'ok', text: `Imported ${result.teachers} teachers, ${result.sections} sections and ${result.curriculumClasses} classes.` })
      status.reload()
    } catch (e) {
      setMessage({ tone: 'error', text: e.message })
    } finally {
      setBusy(false)
    }
  }

  return (
    <>
      <PageHeader title="Database">
        Teachers, sections and the curriculum live in the database. Edit them on their own pages and the next timetable uses the changes.
      </PageHeader>

      {message && (
        <div className={`notice ${message.tone === 'ok' ? 'notice-ok' : 'notice-error'}`} role="status">
          {message.text}
        </div>
      )}

      <Panel title="What is stored" actions={status.data && <Badge tone="ok">Source: {status.data.source}</Badge>}>
        <ErrorBox error={status.error} onRetry={status.reload} />
        {status.loading ? <Loading /> : <KeyValues data={status.data} />}
      </Panel>

      <Panel
        title="Reload from the JSON files"
        note="On first start the database is filled from the JSON files automatically. Use this only to start over from those files."
      >
        <button className="btn btn-danger" onClick={reimport} disabled={busy}>
          {busy ? 'Importing' : 'Replace database with JSON files'}
        </button>
      </Panel>
    </>
  )
}
