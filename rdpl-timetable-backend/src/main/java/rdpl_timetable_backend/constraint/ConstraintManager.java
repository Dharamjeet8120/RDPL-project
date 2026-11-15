
package rdpl_timetable_backend.constraint;

import java.util.List;

import org.springframework.stereotype.Component;

import com.google.ortools.sat.CpModel;

import rdpl_timetable_backend.solver.SchedulingVariable;

@Component
public class ConstraintManager {

	private final SectionNoDoubleBookingConstraint sectionNoDoubleBookingConstraint;

	private final TeacherNoDoubleBookingConstraint teacherNoDoubleBookingConstraint;

	private final MaxConsecutiveSubjectConstraint maxConsecutiveSubjectConstraint;

	private final SubjectNoSameDayConstraint subjectNoSameDayConstraint;

	public ConstraintManager(SectionNoDoubleBookingConstraint sectionNoDoubleBookingConstraint,
			TeacherNoDoubleBookingConstraint teacherNoDoubleBookingConstraint,
			MaxConsecutiveSubjectConstraint maxConsecutiveSubjectConstraint,
			SubjectNoSameDayConstraint subjectNoSameDayConstraint) {

		this.sectionNoDoubleBookingConstraint = sectionNoDoubleBookingConstraint;

		this.teacherNoDoubleBookingConstraint = teacherNoDoubleBookingConstraint;

		this.maxConsecutiveSubjectConstraint = maxConsecutiveSubjectConstraint;

		this.subjectNoSameDayConstraint = subjectNoSameDayConstraint;
	}

	public void applyAll(CpModel model, List<SchedulingVariable> variables) {

		sectionNoDoubleBookingConstraint.apply(model, variables);

		teacherNoDoubleBookingConstraint.apply(model, variables);

		maxConsecutiveSubjectConstraint.apply(model, variables);

		subjectNoSameDayConstraint.apply(model, variables);
	}
}
