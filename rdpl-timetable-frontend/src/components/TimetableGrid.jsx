import { DAYS, dayKey, dayShort, subjectColor, trimTime } from '../utils.js'

// Weekly grid: one row per period, one column per day.
// `slots` is the list from /time-slots, used for period times and to grey out
// periods a day does not have (Monday to Thursday have 8, Friday and Saturday 10).
export default function TimetableGrid({ entries, slots, detailOf }) {
  const cells = new Map()
  entries.forEach((e) => cells.set(`${dayKey(e.day)}|${e.periodNumber}`, e))

  const slotMap = new Map()
  let maxPeriod = 0
  ;(slots || []).forEach((s) => {
    slotMap.set(`${dayKey(s.day)}|${s.periodNumber}`, s)
    maxPeriod = Math.max(maxPeriod, s.periodNumber)
  })
  entries.forEach((e) => {
    maxPeriod = Math.max(maxPeriod, e.periodNumber || 0)
  })
  const hasSlots = slotMap.size > 0
  const periods = Array.from({ length: maxPeriod }, (_, i) => i + 1)

  const timeFor = (period) => {
    const first = DAYS.map((d) => slotMap.get(`${dayKey(d)}|${period}`)).find(Boolean)
    return first ? `${trimTime(first.startTime)}–${trimTime(first.endTime)}` : ''
  }

  return (
    <div className="grid-wrap">
      <table className="tt-grid">
        <thead>
          <tr>
            <th className="tt-corner">Period</th>
            {DAYS.map((d) => (
              <th key={d} scope="col">
                {dayShort(d)}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {periods.map((p) => (
            <tr key={p}>
              <th scope="row" className="tt-period">
                <span className="tt-num">{p}</span>
                <span className="tt-time">{timeFor(p)}</span>
              </th>
              {DAYS.map((d) => {
                const key = `${dayKey(d)}|${p}`
                const entry = cells.get(key)
                const slot = slotMap.get(key)
                if (!entry) {
                  const off = hasSlots && !slot
                  return (
                    <td key={d} className={off ? 'tt-cell tt-off' : 'tt-cell tt-free'}>
                      {off ? '' : 'Free'}
                    </td>
                  )
                }
                const isActivity = String(entry.type || '').toUpperCase() === 'ACTIVITY'
                return (
                  <td
                    key={d}
                    className={`tt-cell tt-lesson ${isActivity ? 'tt-activity' : ''}`}
                    style={subjectColor(entry.subjectName)}
                    title={slot ? `${trimTime(slot.startTime)}–${trimTime(slot.endTime)}` : undefined}
                  >
                    <span className="tt-subject">{entry.subjectName}</span>
                    <span className="tt-detail">{detailOf(entry)}</span>
                  </td>
                )
              })}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
