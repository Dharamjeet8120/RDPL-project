
package rdpl_timetable_backend.model;

public class TimetableSlot {

    private String sectionId;

    private String day;

    private int periodNumber;

    private String periodLabel;

    private String subjectName;

    private String teacherId;

    private String type;

    public TimetableSlot() {
    }

    public TimetableSlot(
            String sectionId,
            String day,
            int periodNumber,
            String periodLabel,
            String subjectName,
            String teacherId,
            String type) {

        this.sectionId = sectionId;
        this.day = day;
        this.periodNumber = periodNumber;
        this.periodLabel = periodLabel;
        this.subjectName = subjectName;
        this.teacherId = teacherId;
        this.type = type;
    }

    public String getSectionId() {
        return sectionId;
    }

    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public int getPeriodNumber() {
        return periodNumber;
    }

    public void setPeriodNumber(int periodNumber) {
        this.periodNumber = periodNumber;
    }

    public String getPeriodLabel() {
        return periodLabel;
    }

    public void setPeriodLabel(String periodLabel) {
        this.periodLabel = periodLabel;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
