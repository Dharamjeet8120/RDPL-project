#!/usr/bin/env python3
"""
Independent checker for a generated timetable.

Usage:
  python3 verify_timetable.py http://localhost:8085 [timetableId]      # fetch from running backend
  python3 verify_timetable.py entries.json                              # check a saved JSON file

Needs data/curriculum.json, data/sections.json, data/teachers.json
(default: ../src/main/resources/data/ ; override with DATA_DIR=...).
"""
import collections, json, os, re, sys, urllib.request

DATA = os.environ.get("DATA_DIR", os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "src", "main", "resources", "data"))
norm = lambda s: re.sub(r"\s+", " ", re.sub(r"\s*/\s*", "/", (s or "").lower())).strip()
load = lambda n: json.load(open(os.path.join(DATA, n), encoding="utf-8-sig"))

def get(url):
    with urllib.request.urlopen(url, timeout=60) as r:
        return json.loads(r.read().decode("utf-8"))

def fetch_entries(arg, tid):
    if os.path.isfile(arg):
        return json.load(open(arg))
    if tid is None:
        hist = get(arg + "/api/timetable/history")
        if not hist: sys.exit("FAIL: no generated timetable in history")
        tid = hist[0]["id"]
    print("Using timetable id", tid)
    return get(f"{arg}/api/timetable/history/{tid}/entries")

def main():
    if len(sys.argv) < 2: sys.exit(__doc__)
    entries = fetch_entries(sys.argv[1], sys.argv[2] if len(sys.argv) > 2 else None)
    curriculum = {c["className"]: c["items"] for c in load("curriculum.json")["classes"]}
    sections = {s["id"]: s for s in load("sections.json")["sections"] if s["className"] in curriculum}
    teachers = {t["teacherId"]: t for t in load("teachers.json")["teachers"]}
    errors, ok = [], []
    def check(name, bad):
        (errors if bad else ok).append(name)
        print(("FAIL " if bad else "PASS ") + name)
        for b in bad[:8]: print("     -", b)
        if len(bad) > 8: print(f"     ... and {len(bad)-8} more")

    day_of = lambda e: (e["day"] or "").upper()
    slots_per_day = {"MONDAY": 8, "TUESDAY": 8, "WEDNESDAY": 8, "THURSDAY": 8, "FRIDAY": 10, "SATURDAY": 10}
    total_slots = sum(slots_per_day.values())

    check(f"entry count = {len(sections)} sections x {total_slots} slots = {len(sections)*total_slots}",
          [] if len(entries) == len(sections) * total_slots else [f"found {len(entries)}"])

    # 1. every section has exactly one lesson in every slot
    by_sec = collections.defaultdict(list)
    for e in entries: by_sec[e["sectionId"]].append(e)
    bad = []
    for sid in sections:
        seen = collections.Counter((day_of(e), e["periodNumber"]) for e in by_sec.get(sid, []))
        for d, n in slots_per_day.items():
            for p in range(1, n + 1):
                if seen[(d, p)] != 1: bad.append(f"{sid} {d} P{p}: {seen[(d, p)]} lessons")
    check("every section has exactly one lesson in every slot", bad)

    # 2. no teacher double-booked
    tslot = collections.defaultdict(list)
    for e in entries: tslot[(e["teacherId"], day_of(e), e["periodNumber"])].append(e["sectionId"])
    check("no teacher teaches two sections in the same slot",
          [f"{k[0]} {k[1]} P{k[2]}: {v}" for k, v in tslot.items() if len(v) > 1])

    # 3. subject quotas per section
    bad = []
    for sid, sec in sections.items():
        got = collections.Counter((norm(e["subjectName"]), norm(e["type"])) for e in by_sec.get(sid, []))
        want = {(norm(i["name"]), norm(i["type"])): i["periodsPerWeek"] for i in curriculum[sec["className"]]}
        for k, w in want.items():
            if got.get(k, 0) != w: bad.append(f"{sid} {k[0]}: expected {w}, got {got.get(k, 0)}")
        for k in got:
            if k not in want: bad.append(f"{sid} {k[0]} not in curriculum")
    check("each section gets exactly its curriculum periods per subject", bad)

    # 4. teacher eligibility
    bad = set()
    for e in entries:
        sec = sections.get(e["sectionId"]); t = teachers.get(e["teacherId"])
        if not sec or not t: bad.add(f"unknown section/teacher {e['sectionId']} / {e['teacherId']}"); continue
        match = [a for a in t["assignments"] if a["className"] == sec["className"]
                 and norm(a["subjectOrActivity"]) == norm(e["subjectName"]) and norm(a["type"]) == norm(e["type"])]
        if not match: bad.add(f"{t['teacherName']} is not eligible for {sec['className']} {e['subjectName']}")
    check("every lesson uses a teacher eligible for that class/subject", sorted(bad))

    # 5. teacher daily and weekly load
    daily = collections.Counter((e["teacherId"], day_of(e)) for e in entries)
    check("no teacher exceeds 10 periods in a day", [f"{k}: {v}" for k, v in daily.items() if v > 10])
    weekly = collections.Counter(e["teacherId"] for e in entries)
    check(f"no teacher exceeds {total_slots} periods per week", [f"{k}: {v}" for k, v in weekly.items() if v > total_slots])

    print("\nTeacher load (top 10):")
    for tid, n in weekly.most_common(10): print(f"  {teachers[tid]['teacherName']:<28}{n}")
    print(f"\n{len(ok)} checks passed, {len(errors)} failed")
    sys.exit(1 if errors else 0)

if __name__ == "__main__":
    main()
