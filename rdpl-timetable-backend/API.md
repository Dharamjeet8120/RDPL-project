# RDPL Timetable Backend – API list (Base URL http://localhost:8085)

| # | Method | URL | Purpose |
|---|---|---|---|
| 1 | GET | /api/timetable/teachers | Teachers list |
| 2 | GET | /api/timetable/teachers/count | Total teachers |
| 3 | GET | /api/timetable/sections | Sections list |
| 4 | GET | /api/timetable/sections/count | Total sections |
| 5 | GET | /api/timetable/curriculum | Curriculum |
| 6 | GET | /api/timetable/curriculum/count | Curriculum count |
| 7 | GET | /api/timetable/time-slots | Time slots (52 per week) |
| 8 | GET | /api/timetable/time-slots/count | Time slot count |
| 9 | GET | /api/timetable/bell-schedule | Bell schedule |
| 10 | GET | /api/timetable/bell-schedule/capacity | Weekly capacity |
| 11 | GET | /api/timetable/load-all-data | Load all data |
| 12 | GET | /api/timetable/school-sample | School sample |
| 13 | GET | /api/timetable/scheduling-rules | Scheduling rules |
| 14 | GET | /api/timetable/quotas | Subject quotas |
| 15 | GET | /api/timetable/quotas/validate | Validate quotas |
| 16 | GET | /api/timetable/quotas/missing-sections | Sections without curriculum |
| 17 | GET | /api/timetable/quotas/mapping-report | Quota mapping report |
| 18 | GET | /api/timetable/teacher-assignments/eligible?className=Class 1&subjectName=English&type=SUBJECT | Eligible teachers (type: SUBJECT or ACTIVITY; THEORY is accepted as SUBJECT) |
| 19 | GET | /api/timetable/teacher-assignments/unassigned | Quotas with no teacher |
| 20 | GET | /api/timetable/validate | Validate input data |
| 21 | GET | /api/timetable/solver/test?sectionId=&subjectName=&teacherId= | Solver test |
| 22 | POST | /api/timetable/generate | Generate timetable |
| 23 | POST | /api/timetable/history?academicYear=2026-2027&status=OPTIMAL | Save history |
| 24 | GET | /api/timetable/history | All history |
| 25 | GET | /api/timetable/history/academic-year/{year} | History by year |
| 26 | GET | /api/timetable/history/{id} | History by id |
| 27 | DELETE | /api/timetable/history/{id} | Delete history |
| 28 | POST | /api/timetable/history/{id}/entries | Save entries (JSON array) |
| 29 | GET | /api/timetable/history/{id}/entries | Get entries |
| 30 | GET | /api/timetable/history/{id}/entries/count | Count entries |
| 31 | GET | /api/timetable/text/latest?view=section&id=<sectionId or name> | Latest section timetable (text) |
| 32 | GET | /api/timetable/text/latest?view=teacher&id=<teacherId or name> | Latest teacher timetable (text) |
| 33 | GET | /api/timetable/text/{historyId}?view=section&id= | Section timetable by history id |
| 34 | GET | /api/timetable/text/{historyId}?view=teacher&id= | Teacher timetable by history id |

Section ids look like `class-2-lily`; teacher ids like `RVS159`. `id` is optional (omit it to print all).
Errors are returned as JSON: 400 bad request, 404 not found, 422 solver/data problem.

Test everything: `./api-tests/test-api.sh` (needs the backend and MySQL running).
