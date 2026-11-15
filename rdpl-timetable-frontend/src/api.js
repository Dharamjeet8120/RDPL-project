// All backend calls live here. Base is empty in development (Vite proxy).
const BASE = import.meta.env.VITE_API_BASE || ''
const API = `${BASE}/api/timetable`

async function request(path, { method = 'GET', body, text = false } = {}) {
  const options = { method, headers: {} }
  if (body !== undefined) {
    options.headers['Content-Type'] = 'application/json'
    options.body = JSON.stringify(body)
  }
  let response
  try {
    response = await fetch(`${API}${path}`, options)
  } catch {
    throw new Error('Cannot reach the backend. Check that it is running on port 8085.')
  }
  const raw = await response.text()
  if (!response.ok) {
    let message = `Request failed (${response.status})`
    try {
      const data = JSON.parse(raw)
      if (data.message) message = data.message
    } catch {
      if (raw) message = raw.slice(0, 300)
    }
    const error = new Error(message)
    error.status = response.status
    throw error
  }
  if (text) return raw
  if (!raw) return null
  try {
    return JSON.parse(raw)
  } catch {
    return raw
  }
}

const q = (params) => {
  const s = new URLSearchParams()
  Object.entries(params).forEach(([k, v]) => {
    if (v !== undefined && v !== null && v !== '') s.set(k, v)
  })
  const str = s.toString()
  return str ? `?${str}` : ''
}

export const api = {
  // Master data
  teachers: () => request('/teachers'),
  teacherCount: () => request('/teachers/count'),
  sections: () => request('/sections'),
  sectionCount: () => request('/sections/count'),
  curriculum: () => request('/curriculum'),
  curriculumCount: () => request('/curriculum/count'),
  timeSlots: () => request('/time-slots'),
  timeSlotCount: () => request('/time-slots/count'),
  bellSchedule: () => request('/bell-schedule'),
  bellCapacity: () => request('/bell-schedule/capacity'),
  // Data and configuration
  loadAllData: () => request('/load-all-data'),
  schoolSample: () => request('/school-sample'),
  schedulingRules: () => request('/scheduling-rules'),
  quotas: () => request('/quotas'),
  validateQuotas: () => request('/quotas/validate'),
  missingSections: () => request('/quotas/missing-sections'),
  mappingReport: () => request('/quotas/mapping-report'),
  // Teacher assignments
  eligible: (className, subjectName, type) =>
    request(`/teacher-assignments/eligible${q({ className, subjectName, type })}`),
  unassigned: () => request('/teacher-assignments/unassigned'),
  assignmentValidation: () => request('/teachers/assignment-validation'),
  // Validation and solver
  validate: () => request('/validate'),
  solverTest: (sectionId, subjectName, teacherId) =>
    request(`/solver/test${q({ sectionId, subjectName, teacherId })}`),
  generate: () => request('/generate', { method: 'POST' }),
  // History
  history: () => request('/history'),
  historyByYear: (year) => request(`/history/academic-year/${encodeURIComponent(year)}`),
  historyById: (id) => request(`/history/${id}`),
  saveHistory: (academicYear, status) =>
    request(`/history${q({ academicYear, status })}`, { method: 'POST' }),
  deleteHistory: (id) => request(`/history/${id}`, { method: 'DELETE' }),
  // Entries
  entries: (id) => request(`/history/${id}/entries`),
  entryCount: (id) => request(`/history/${id}/entries/count`),
  saveEntries: (id, entries) => request(`/history/${id}/entries`, { method: 'POST', body: entries }),
  // Database management
  dataStatus: () => request('/data/status'),
  importData: () => request('/data/import', { method: 'POST' }),
  teacher: (id) => request(`/teachers/${encodeURIComponent(id)}`),
  createTeacher: (teacher) => request('/teachers', { method: 'POST', body: teacher }),
  updateTeacher: (id, teacher) => request(`/teachers/${encodeURIComponent(id)}`, { method: 'PUT', body: teacher }),
  replaceAssignments: (id, rows) =>
    request(`/teachers/${encodeURIComponent(id)}/assignments`, { method: 'PUT', body: rows }),
  deleteTeacher: (id) => request(`/teachers/${encodeURIComponent(id)}`, { method: 'DELETE' }),
  createSection: (section) => request('/sections', { method: 'POST', body: section }),
  updateSection: (id, section) => request(`/sections/${encodeURIComponent(id)}`, { method: 'PUT', body: section }),
  deleteSection: (id) => request(`/sections/${encodeURIComponent(id)}`, { method: 'DELETE' }),
  saveCurriculum: (className, curriculum) =>
    request(`/curriculum/${encodeURIComponent(className)}`, { method: 'PUT', body: curriculum }),
  deleteCurriculum: (className) => request(`/curriculum/${encodeURIComponent(className)}`, { method: 'DELETE' }),
  // Text timetable
  textLatest: (view, id) => request(`/text/latest${q({ view, id })}`, { text: true }),
  textById: (historyId, view, id) => request(`/text/${historyId}${q({ view, id })}`, { text: true }),
}
