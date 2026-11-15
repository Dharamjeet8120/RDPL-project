import { useState } from 'react'
import { api } from '../api.js'
import { useAsync } from '../hooks.js'
import { Badge, ErrorBox, KeyValues, Loading, PageHeader, Panel, statusTone, Table, TextList } from '../components/ui.jsx'

const settle = (promise) => promise.then((value) => ({ ok: true, value })).catch((error) => ({ ok: false, error }))

async function loadChecks() {
  const [validation, quotas, unassigned, missing, mapping, assignments] = await Promise.all([
    settle(api.validate()),
    settle(api.validateQuotas()),
    settle(api.unassigned()),
    settle(api.missingSections()),
    settle(api.mappingReport()),
    settle(api.assignmentValidation()),
  ])
  return { validation, quotas, unassigned, missing, mapping, assignments }
}

function Section({ result, title, note, children }) {
  return (
    <Panel title={title} note={note}>
      {result.ok ? children(result.value) : <ErrorBox error={result.error} />}
    </Panel>
  )
}

export default function Checks() {
  const { data, loading, error, reload } = useAsync(loadChecks, [])
  const [statusFilter, setStatusFilter] = useState('ALL')

  if (loading) return <Loading label="Running checks" />
  if (error) return <ErrorBox error={error} onRetry={reload} />

  const unassigned = data.unassigned.ok ? data.unassigned.value : []
  const validation = data.validation.ok ? data.validation.value : null

  return (
    <>
      <PageHeader
        title="Checks"
        actions={
          <button className="btn" onClick={reload}>
            Run checks again
          </button>
        }
      >
        Fix everything marked here before you generate. A clean page means the solver has what it needs.
      </PageHeader>

      <Section result={data.validation} title="Input data" note="Sections, curriculum, bell schedule and teachers together.">
        {(v) => (
          <>
            <p>
              <Badge tone={v.valid ? 'ok' : 'bad'}>{v.valid ? 'Valid' : 'Not valid'}</Badge> {v.totalErrors} errors, {v.totalWarnings} warnings
            </p>
            <TextList items={v.errors} tone="bad" />
            <TextList items={v.warnings} tone="warn" />
          </>
        )}
      </Section>

      <Section result={data.unassigned} title="Subjects without a teacher" note="Each section needs at least one eligible teacher for every subject.">
        {(list) =>
          list.length === 0 ? (
            <p>
              <Badge tone="ok">All covered</Badge> Every subject in every section has an eligible teacher.
            </p>
          ) : (
            <Table
              rows={list}
              rowKey={(r, i) => `${r.sectionId}-${r.subjectName}-${i}`}
              columns={[
                { key: 'sectionId', label: 'Section' },
                { key: 'className', label: 'Class' },
                { key: 'subjectName', label: 'Subject' },
                { key: 'type', label: 'Type' },
                { key: 'periodsPerWeek', label: 'Periods', numeric: true },
              ]}
            />
          )
        }
      </Section>

      <Section result={data.quotas} title="Subject quotas" note="Periods each section needs compared with the weekly slots.">
        {(q) => <KeyValues data={q} />}
      </Section>

      <Section result={data.missing} title="Sections without a curriculum" note="Pre-primary sections have no curriculum yet, so no lessons are generated for them.">
        {(list) =>
          list.length === 0 ? (
            <p>
              <Badge tone="ok">None</Badge> Every section has a curriculum.
            </p>
          ) : (
            <Table
              rows={list}
              rowKey={(r) => r.id}
              columns={[
                { key: 'className', label: 'Class' },
                { key: 'sectionName', label: 'Section' },
                { key: 'curriculumKey', label: 'Curriculum key' },
              ]}
            />
          )
        }
      </Section>

      <Section result={data.mapping} title="Curriculum mapping" note="How each section links to its curriculum.">
        {(list) => {
          const statuses = ['ALL', ...new Set(list.map((r) => r.status))]
          const rows = statusFilter === 'ALL' ? list : list.filter((r) => r.status === statusFilter)
          return (
            <>
              <div className="toolbar">
                <label>
                  Status
                  <select value={statusFilter} onChange={(e) => setStatusFilter(e.target.value)}>
                    {statuses.map((s) => (
                      <option key={s}>{s}</option>
                    ))}
                  </select>
                </label>
              </div>
              <Table
                rows={rows}
                rowKey={(r) => r.sectionId}
                columns={[
                  { key: 'className', label: 'Class' },
                  { key: 'sectionName', label: 'Section' },
                  { key: 'curriculumKey', label: 'Curriculum key' },
                  { key: 'status', label: 'Status', render: (r) => <Badge tone={statusTone(r.status)}>{r.status}</Badge> },
                  { key: 'weeklyPeriods', label: 'Weekly periods', numeric: true },
                ]}
              />
            </>
          )
        }}
      </Section>

      <Section result={data.assignments} title="Teacher assignments" note="Summary of the teacher data file.">
        {(a) => {
          const { warnings, readyForTeacherScheduling, ...rest } = a
          return (
            <>
              <KeyValues data={rest} />
              <TextList items={warnings} tone="warn" />
            </>
          )
        }}
      </Section>

      <p className="muted pad">
        {validation && unassigned.length === 0 && validation.valid
          ? 'All checks are clear. You can generate the timetable.'
          : 'Some checks need attention before you generate.'}
      </p>
    </>
  )
}
