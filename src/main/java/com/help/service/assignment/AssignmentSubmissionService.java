package com.help.service.assignment;

import com.help.model.person.Person;
import com.help.model.assignment.AssignmentSubmission;

import java.util.List;

public interface AssignmentSubmissionService {
    List<AssignmentSubmission> getByAssignmentIdAndBySubmitter(Long assignmentId, Person submitter);

    List<AssignmentSubmission> getByAssignmentId(Long assignmentId);

    AssignmentSubmission getByAssignmentIdAndId(Long assignmentId, Long id);

    AssignmentSubmission save(AssignmentSubmission assignmentSubmission);
}
