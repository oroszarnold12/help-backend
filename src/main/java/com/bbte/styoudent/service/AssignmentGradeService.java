package com.bbte.styoudent.service;

import com.bbte.styoudent.model.AssignmentGrade;
import com.bbte.styoudent.model.Person;

import java.util.List;

public interface AssignmentGradeService {
    List<AssignmentGrade> getByAssignmentIdAndBySubmitter(Long assignmentId, Person submitter);

    List<AssignmentGrade> getByAssignmentId(Long assignmentId);

    AssignmentGrade save(AssignmentGrade assignmentGrade);

    boolean checkIfExistsByAssignmentIdAndSubmitter(Long assignmentId, Person submitter);

    void deleteByAssignmentIdAndSubmitterId(Long assignmentId, Long submitterId);
}
