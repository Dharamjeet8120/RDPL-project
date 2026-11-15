import { api } from '../api.js'
import { useAsync } from '../hooks.js'
import { DAYS, dayKey, dayShort, trimTime } from '../utils.js'
import { ErrorBox, Loading, PageHeader, Panel } from '../components/ui.jsx'

export default function Schedule() {
  const slots = useAsync(() => api.timeSlots(), [])
  const bell = useAsync(() => api.bellSchedule(), [])

  const map = new Map()
  let maxPeriod = 0
  ;(slots.data || []).forEach((s) => {
    map.set(`${dayKey(s.day)}|${s.periodNumber}`, s)
    maxPeriod = Math.max(maxPeriod, s.periodNumber)
  })
  const periods = Array.from({ length: maxPeriod }, (_, i) => i + 1)

  return (
    <>
      <PageHeader title="Bell schedule">
        {bell.data ? `${bell.data.scheduleName}, ${bell.data.weeklyTeachingCapacity} teaching periods a week.` : 'When each period starts and ends.'}
      </PageHeader>
      <ErrorBox error={slots.error} onRetry={slots.reload} />
      <ErrorBox error={bell.error} onRetry={bell.reload} />
      {slots.loading ? (
        <Loading />
      ) : (
        <Panel title="Periods by day" note={`${slots.data.length} time slots`}>
          <div className="grid-wrap">
            <table className="tt-grid">
              <thead>
                <tr>
                  <th className="tt-corner">Period</th>
                  {DAYS.map((d) => (
                    <th key={d}>{dayShort(d)}</th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {periods.map((p) => (
                  <tr key={p}>
                    <th scope="row" className="tt-period">
                      <span className="tt-num">{p}</span>
                    </th>
                    {DAYS.map((d) => {
                      const s = map.get(`${dayKey(d)}|${p}`)
                      return (
                        <td key={d} className={s ? 'tt-cell tt-time-cell' : 'tt-cell tt-off'}>
                          {s ? `${trimTime(s.startTime)}–${trimTime(s.endTime)}` : ''}
                        </td>
                      )
                    })}
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </Panel>
      )}
    </>
  )
}
