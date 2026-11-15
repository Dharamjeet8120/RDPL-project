import { NavLink, Route, Routes } from 'react-router-dom'
import Overview from './pages/Overview.jsx'
import Generate from './pages/Generate.jsx'
import Timetable from './pages/Timetable.jsx'
import History from './pages/History.jsx'
import Teachers from './pages/Teachers.jsx'
import Classes from './pages/Classes.jsx'
import Schedule from './pages/Schedule.jsx'
import Checks from './pages/Checks.jsx'
import Tools from './pages/Tools.jsx'
import Database from './pages/Database.jsx'

const NAV = [
  { to: '/', label: 'Overview', end: true },
  { to: '/generate', label: 'Generate' },
  { to: '/timetable', label: 'Timetable' },
  { to: '/history', label: 'History' },
  { group: 'School data' },
  { to: '/teachers', label: 'Teachers' },
  { to: '/classes', label: 'Classes and subjects' },
  { to: '/schedule', label: 'Bell schedule' },
  { to: '/database', label: 'Database' },
  { group: 'Before you generate' },
  { to: '/checks', label: 'Checks' },
  { to: '/tools', label: 'Find a teacher' },
]

export default function App() {
  return (
    <div className="shell">
      <aside className="sidebar">
        <div className="brand">
          <span className="brand-mark" aria-hidden="true">
            RT
          </span>
          <div>
            <strong>RDPL Timetable</strong>
            <span>Academic year 2026-2027</span>
          </div>
        </div>
        <nav aria-label="Main">
          {NAV.map((item, i) =>
            item.group ? (
              <p key={i} className="nav-group">
                {item.group}
              </p>
            ) : (
              <NavLink key={item.to} to={item.to} end={item.end} className="nav-link">
                {item.label}
              </NavLink>
            ),
          )}
        </nav>
      </aside>
      <main className="main">
        <Routes>
          <Route path="/" element={<Overview />} />
          <Route path="/generate" element={<Generate />} />
          <Route path="/timetable" element={<Timetable />} />
          <Route path="/history" element={<History />} />
          <Route path="/teachers" element={<Teachers />} />
          <Route path="/classes" element={<Classes />} />
          <Route path="/schedule" element={<Schedule />} />
          <Route path="/checks" element={<Checks />} />
          <Route path="/tools" element={<Tools />} />
          <Route path="/database" element={<Database />} />
          <Route path="*" element={<p className="muted pad">This page does not exist.</p>} />
        </Routes>
      </main>
    </div>
  )
}
