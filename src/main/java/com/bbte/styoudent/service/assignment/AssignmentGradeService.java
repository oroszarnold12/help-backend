package com.bbte.styoudent.service.assignment;

import com.bbte.styoudent.model.assignment.AssignmentGrade;
import com.bbte.styoudent.model.person.Person;

import java.util.List;

public interface AssignmentGradeService {
    List<AssignmentGrade> getByAssignmentIdAndBySubmitter(Long assignmentId, Person submitter);

    List<AssignmentGrade> getByAssignmentId(Long assignmentId);

    AssignmentGrade save(AssignmentGrade assignmentGrade);

    boolean checkIfExistsByAssignmentIdAndSubmitter(Long assignmentId, Person submitter);
}
