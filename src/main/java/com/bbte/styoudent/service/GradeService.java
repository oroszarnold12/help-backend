package com.bbte.styoudent.service;

import com.bbte.styoudent.model.Grade;
import com.bbte.styoudent.model.Person;

import java.util.List;

public interface GradeService {
    List<Grade> getByAssignmentIdAndBySubmitter(Long assignmentId, Person submitter);

    List<Grade> getByAssignmentId(Long assignmentId);

    Grade save(Grade grade);

    boolean checkIfExistsByAssignmentIdAndSubmitter(Long assignmentId, Person submitter);

    void deleteByAssignmentIdAndSubmitterId(Long assignmentId, Long submitterId);
}
