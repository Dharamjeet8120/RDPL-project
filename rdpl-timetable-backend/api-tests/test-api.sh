#!/usr/bin/env bash
# Tests all 34 APIs of the RDPL timetable backend.
# Usage: ./api-tests/test-api.sh [BASE_URL]      (default http://localhost:8085)
BASE="${1:-http://localhost:8085}"
API="$BASE/api/timetable"
HERE="$(cd "$(dirname "$0")" && pwd)"
PASS=0; FAIL=0

# t "No. name" METHOD URL EXPECTED_HTTP [grep-pattern] [json-body]
t() {
  local name="$1" method="$2" url="$3" code="$4" pat="$5" body="$6"
  local args=(-s -m 200 -X "$method" -w '\n%{http_code}')
  [ -n "$body" ] && args+=(-H 'Content-Type: application/json' -d "$body")
  LAST=$(curl "${args[@]}" "$url")
  local status; status=$(echo "$LAST" | tail -n1); LAST=$(echo "$LAST" | sed '$d')
  if [ "$status" != "$code" ]; then echo "FAIL  $name -> HTTP $status (expected $code): $(echo "$LAST" | head -c 160)"; FAIL=$((FAIL+1)); return; fi
  if [ -n "$pat" ] && ! echo "$LAST" | grep -Eq "$pat"; then echo "FAIL  $name -> response missing /$pat/: $(echo "$LAST" | head -c 160)"; FAIL=$((FAIL+1)); return; fi
  echo "PASS  $name"; PASS=$((PASS+1))
}

echo "== 1. Master data =="
t "1  GET teachers"                    GET "$API/teachers" 200 '"teacherId"'
t "2  GET teachers/count"              GET "$API/teachers/count" 200 '^56$'
t "3  GET sections"                    GET "$API/sections" 200 '"className"'
t "4  GET sections/count"              GET "$API/sections/count" 200 '^[0-9]+$'
t "5  GET curriculum"                  GET "$API/curriculum" 200 '"className"'
t "6  GET curriculum/count"            GET "$API/curriculum/count" 200 '^[0-9]+$'
t "7  GET time-slots"                  GET "$API/time-slots" 200 '"day"'
t "8  GET time-slots/count (52)"       GET "$API/time-slots/count" 200 '^52$'
t "9  GET bell-schedule"               GET "$API/bell-schedule" 200
t "10 GET bell-schedule/capacity (52)" GET "$API/bell-schedule/capacity" 200 '^52$'

echo "== 2. Data loading & configuration =="
t "11 GET load-all-data"               GET "$API/load-all-data" 200
t "12 GET school-sample"               GET "$API/school-sample" 200
t "13 GET scheduling-rules"            GET "$API/scheduling-rules" 200
t "14 GET quotas"                      GET "$API/quotas" 200 '"sectionId"'
t "15 GET quotas/validate"             GET "$API/quotas/validate" 200
t "16 GET quotas/missing-sections"     GET "$API/quotas/missing-sections" 200
t "17 GET quotas/mapping-report"       GET "$API/quotas/mapping-report" 200

echo "== 3. Teacher assignments =="
t "18 GET eligible (type=THEORY)"      GET "$API/teacher-assignments/eligible?className=Class%201&subjectName=English&type=THEORY" 200 'Priyanka Rana'
t "18b GET eligible (type=SUBJECT)"    GET "$API/teacher-assignments/eligible?className=Class%201&subjectName=English&type=SUBJECT" 200 'Priyanka Rana'
t "19 GET unassigned (must be [])"     GET "$API/teacher-assignments/unassigned" 200 '^\[\]$'

echo "== 4. Validation & solver =="
t "20 GET validate"                    GET "$API/validate" 200
t "21 GET solver/test"                 GET "$API/solver/test?sectionId=1&subjectName=English&teacherId=1" 200
echo "     generating timetable (up to ~60s) ..."
t "22 POST generate"                   POST "$API/generate" 200 'OPTIMAL|FEASIBLE'

echo "== 5. History =="
t "24 GET history"                     GET "$API/history" 200 '"id"'
TID=$(echo "$LAST" | python3 -c 'import sys,json; h=json.load(sys.stdin); print(max(x["id"] for x in h))')
echo "     latest timetable id = $TID"
t "25 GET history/academic-year"       GET "$API/history/academic-year/2026-2027" 200
t "26 GET history/{id}"                GET "$API/history/$TID" 200 '"id"'
t "23 POST history (manual save)"      POST "$API/history?academicYear=2026-2027&status=TEST" 201 '"id"'
TMP=$(echo "$LAST" | python3 -c 'import sys,json; print(json.load(sys.stdin)["id"])')

echo "== 6. Entries =="
t "28 POST history/{id}/entries"       POST "$API/history/$TMP/entries" 200 'class-1-marigold' '[{"sectionId":"class-1-marigold","subjectName":"English","teacherId":"E2904","type":"SUBJECT","day":"Monday","periodNumber":1}]'
t "29 GET history/{id}/entries"        GET "$API/history/$TMP/entries" 200 'E2904'
t "30 GET history/{id}/entries/count"  GET "$API/history/$TMP/entries/count" 200 '^1$'
t "27 DELETE history/{id}"             DELETE "$API/history/$TMP" 204
t "27b GET deleted history -> 404"     GET "$API/history/$TMP" 404
t "30b generated entries = 1560"       GET "$API/history/$TID/entries/count" 200 '^1560$'

echo "== 7. Text display =="
t "31 GET text/latest section"         GET "$API/text/latest?view=section&id=class-1-marigold" 200 'Period'
t "32 GET text/latest teacher"         GET "$API/text/latest?view=teacher&id=RVS159" 200 'Saksham'
t "33 GET text/{id} section"           GET "$API/text/$TID?view=section&id=class-1-marigold" 200 'Period'
t "34 GET text/{id} teacher"           GET "$API/text/$TID?view=teacher&id=RVS159" 200 'Saksham'
t "   text unknown id -> 404"          GET "$API/text/latest?view=section&id=does-not-exist" 404
t "   text unknown timetable -> 404"   GET "$API/text/999999" 404

echo "== Full correctness check of the generated timetable =="
if python3 "$HERE/verify_timetable.py" "$BASE" "$TID"; then echo "PASS  verify_timetable.py"; PASS=$((PASS+1)); else echo "FAIL  verify_timetable.py"; FAIL=$((FAIL+1)); fi

echo; echo "Result: $PASS passed, $FAIL failed"
[ "$FAIL" -eq 0 ]
