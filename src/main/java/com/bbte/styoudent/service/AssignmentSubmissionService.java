package com.bbte.styoudent.service;

import com.bbte.styoudent.model.Person;
import com.bbte.styoudent.model.AssignmentSubmission;

import java.util.List;

public interface AssignmentSubmissionService {
    List<AssignmentSubmission> getByAssignmentIdAndBySubmitter(Long assignmentId, Person submitter);

    List<AssignmentSubmission> getByAssignmentId(Long assignmentId);

    AssignmentSubmission getByAssignmentIdAndId(Long assignmentId, Long id);

    AssignmentSubmission save(AssignmentSubmission assignmentSubmission);
}
