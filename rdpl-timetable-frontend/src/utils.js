export const DAYS = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday']
export const dayShort = (d) => d.slice(0, 3)
export const dayKey = (d) => (d || '').toUpperCase()

// Each subject gets a stable colour so a timetable can be read at a glance.
const SUBJECT_HUES = [
  ['#DDEBFF', '#1D4E8F'],
  ['#FCE3D4', '#8A3B12'],
  ['#E0F2D9', '#2C6B22'],
  ['#F3E1F5', '#7A2B83'],
  ['#FFF0BF', '#7A5A00'],
  ['#D9F1EE', '#17635A'],
  ['#FBDDE3', '#8E2440'],
  ['#E6E4FA', '#3F3A9A'],
  ['#EDEBDD', '#5B5530'],
  ['#D8EEF7', '#175B78'],
  ['#F5E2CF', '#7B4A17'],
  ['#E3EED2', '#496B17'],
]

export function subjectColor(name = '') {
  let hash = 0
  for (let i = 0; i < name.length; i++) hash = (hash * 31 + name.charCodeAt(i)) >>> 0
  const [bg, fg] = SUBJECT_HUES[hash % SUBJECT_HUES.length]
  return { background: bg, color: fg, borderLeftColor: fg }
}

export function formatDateTime(value) {
  if (!value) return ''
  const d = new Date(value)
  if (Number.isNaN(d.getTime())) return String(value)
  return d.toLocaleString(undefined, { dateStyle: 'medium', timeStyle: 'short' })
}

export const trimTime = (t) => (t ? String(t).slice(0, 5) : '')
