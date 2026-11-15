# RDPL Timetable Frontend

React (Vite) interface for the RDPL timetable backend.

## Run

1. Start MySQL and the backend (`mvn spring-boot:run`, port 8085).
2. In this folder:

```bash
npm install
npm run dev
```

3. Open http://localhost:5173

The dev server forwards every `/api` request to http://localhost:8085, so no CORS setup is needed.
If your backend runs elsewhere, start with `VITE_BACKEND_URL=http://host:port npm run dev`.

## Production build

```bash
npm run build      # output in dist/
npm run preview    # serves dist/ on http://localhost:4173 (also proxies /api)
```

To host `dist/` on another server, copy `.env.example` to `.env`, set `VITE_API_BASE` to the backend address
before building, and allow that origin in the backend's CORS settings.

## Pages

| Page | What it does | APIs used |
|---|---|---|
| Overview | Latest timetable, readiness, counts | history, validate, teacher-assignments/unassigned, counts |
| Generate | Runs the solver and shows the result | POST generate, history |
| Timetable | Weekly grid by section or teacher, text view, print | history, entries, sections, teachers, time-slots, text/{id} |
| History | List, filter by year, delete, add a record | history, history/academic-year, entries/count, POST/DELETE history |
| Teachers | Search, add, delete teachers and edit their assignments | teachers, POST/PUT/DELETE teachers, PUT teachers/{id}/assignments |
| Classes and subjects | Sections and weekly curriculum; add, edit, delete | sections, curriculum, POST/PUT/DELETE sections, PUT/DELETE curriculum/{class} |
| Bell schedule | Period times for each day | time-slots, bell-schedule |
| Database | Shows what is stored and re-imports from the JSON files | data/status, POST data/import |
| Checks | Data validation, quotas, missing teachers, mapping | validate, quotas/*, teacher-assignments/unassigned, teachers/assignment-validation |
| Find a teacher | Eligible teachers for a subject, solver test | teacher-assignments/eligible, solver/test |

Other endpoints are wrapped in `src/api.js` for later use: `load-all-data`, `school-sample`, `scheduling-rules`,
`quotas`, `history/{id}`, and `POST history/{id}/entries`.
