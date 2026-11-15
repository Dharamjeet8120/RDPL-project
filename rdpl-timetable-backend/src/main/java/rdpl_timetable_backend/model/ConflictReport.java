
package rdpl_timetable_backend.model;

import java.util.ArrayList;
import java.util.List;

public class ConflictReport {

    private int totalConflicts;

    private int hardConflicts;

    private int softConflicts;

    private List<TimetableConflict> conflicts =
            new ArrayList<>();

    public ConflictReport() {
    }

    public int getTotalConflicts() {
        return totalConflicts;
    }

    public void setTotalConflicts(int totalConflicts) {
        this.totalConflicts = totalConflicts;
    }

    public int getHardConflicts() {
        return hardConflicts;
    }

    public void setHardConflicts(int hardConflicts) {
        this.hardConflicts = hardConflicts;
    }

    public int getSoftConflicts() {
        return softConflicts;
    }

    public void setSoftConflicts(int softConflicts) {
        this.softConflicts = softConflicts;
    }

    public List<TimetableConflict> getConflicts() {
        return conflicts;
    }

    public void setConflicts(List<TimetableConflict> conflicts) {
        this.conflicts = conflicts;
    }

    public void addConflict(TimetableConflict conflict) {

        conflicts.add(conflict);

        totalConflicts = conflicts.size();

        if ("HARD".equalsIgnoreCase(conflict.getSeverity())) {
            hardConflicts++;
        } else {
            softConflicts++;
        }
    }
}
