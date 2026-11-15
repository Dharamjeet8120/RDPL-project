import { api } from '../api.js'
import { useAsync } from '../hooks.js'
import { Badge, Empty, LinkButton, Loading, PageHeader, Panel, statusTone } from '../components/ui.jsx'
import { formatDateTime } from '../utils.js'

const settle = (promise) => promise.then((value) => ({ ok: true, value })).catch((error) => ({ ok: false, error }))

async function loadOverview() {
  const [teachers, sections, capacity, history, validation, unassigned] = await Promise.all([
    settle(api.teacherCount()),
    settle(api.sectionCount()),
    settle(api.bellCapacity()),
    settle(api.history()),
    settle(api.validate()),
    settle(api.unassigned()),
  ])
  return { teachers, sections, capacity, history, validation, unassigned }
}

function Check({ ok, pending, title, detail }) {
  const tone = pending ? 'neutral' : ok ? 'ok' : 'bad'
  return (
    <li className="check-row">
      <Badge tone={tone}>{pending ? 'Unknown' : ok ? 'Ready' : 'Needs attention'}</Badge>
      <div>
        <strong>{title}</strong>
        <span className="muted">{detail}</span>
      </div>
    </li>
  )
}

export default function Overview() {
  const { data, loading, error, reload } = useAsync(loadOverview, [])

  if (loading) return <Loading label="Loading overview" />
  if (error) return <p className="muted pad">{error.message}</p>

  const history = data.history.ok ? data.history.value : null
  const latest = history && history.length ? [...history].sort((a, b) => b.id - a.id)[0] : null
  const validation = data.validation.ok ? data.validation.value : null
  const unassigned = data.unassigned.ok ? data.unassigned.value : null
  const backendDown = !data.teachers.ok && !data.sections.ok

  return (
    <>
      <PageHeader title="Timetable for 2026-2027">
        Generate the weekly timetable for every section, then review it by class or by teacher.
      </PageHeader>

      {backendDown && (
        <div className="notice notice-error" role="alert">
          <div>
            <strong>The backend is not responding.</strong> Start the Spring Boot application on port 8085 and MySQL, then reload.
          </div>
          <button className="btn btn-small" onClick={reload}>
            Reload
          </button>
        </div>
      )}

      <div className="overview-grid">
        <Panel title="Latest timetable">
          {latest ? (
            <div className="latest">
              <div className="latest-main">
                <Badge tone={statusTone(latest.status)}>{latest.status}</Badge>
                <p className="latest-id">Timetable {latest.id}</p>
                <p className="muted">
                  {latest.academicYear}, generated {formatDateTime(latest.generatedAt)}
                </p>
              </div>
              <div className="btn-row">
                <LinkButton primary to={`/timetable?id=${latest.id}`}>
                  Open timetable
                </LinkButton>
                <LinkButton to="/generate">Generate a new one</LinkButton>
              </div>
            </div>
          ) : (
            <Empty
              title="No timetable yet"
              action={
                <LinkButton primary to="/generate">
                  Generate timetable
                </LinkButton>
              }
            >
              Run the generator once and the result appears here.
            </Empty>
          )}
        </Panel>

        <Panel title="Ready to generate?" note="Quick checks on the input data.">
          <ul className="check-list">
            <Check
              pending={!validation}
              ok={validation?.valid}
              title="Input data is valid"
              detail={validation ? `${validation.totalErrors} errors, ${validation.totalWarnings} warnings` : 'Could not load'}
            />
            <Check
              pending={!unassigned}
              ok={unassigned && unassigned.length === 0}
              title="Every subject has a teacher"
              detail={unassigned ? `${unassigned.length} subjects without a teacher` : 'Could not load'}
            />
          </ul>
          <LinkButton to="/checks">See all checks</LinkButton>
        </Panel>
      </div>

      <dl className="facts">
        <div>
          <dt>Teachers</dt>
          <dd>{data.teachers.ok ? data.teachers.value : '-'}</dd>
        </div>
        <div>
          <dt>Sections</dt>
          <dd>{data.sections.ok ? data.sections.value : '-'}</dd>
        </div>
        <div>
          <dt>Periods per week</dt>
          <dd>{data.capacity.ok ? data.capacity.value : '-'}</dd>
        </div>
        <div>
          <dt>Timetables saved</dt>
          <dd>{history ? history.length : '-'}</dd>
        </div>
      </dl>
    </>
  )
}
