package com.help.service.assignment;

import com.help.model.assignment.AssignmentGrade;
import com.help.model.person.Person;

import java.util.List;

public interface AssignmentGradeService {
    List<AssignmentGrade> getByAssignmentIdAndBySubmitter(Long assignmentId, Person submitter);

    List<AssignmentGrade> getByAssignmentId(Long assignmentId);

    AssignmentGrade save(AssignmentGrade assignmentGrade);

    boolean checkIfExistsByAssignmentIdAndSubmitter(Long assignmentId, Person submitter);
}
