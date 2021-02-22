package com.bbte.styoudent.service;

import com.bbte.styoudent.model.Person;
import com.bbte.styoudent.model.Submission;

import java.util.List;

public interface SubmissionService {
    List<Submission> getByAssignmentIdAndBySubmitter(Long assignmentId, Person submitter);

    List<Submission> getByAssignmentId(Long assignmentId);

    Submission getByAssignmentIdAndId(Long assignmentId, Long id);

    Submission save(Submission submission);
}
