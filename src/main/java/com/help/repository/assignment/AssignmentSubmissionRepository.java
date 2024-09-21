package com.help.repository.assignment;

import com.help.model.person.Person;
import com.help.model.assignment.AssignmentSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssignmentSubmissionRepository extends JpaRepository<AssignmentSubmission, Long> {
    List<AssignmentSubmission> findByAssignmentIdAndSubmitter(Long assignmentId, Person submitter);

    List<AssignmentSubmission> findByAssignmentId(Long assignmentId);

    Optional<AssignmentSubmission> findByAssignmentIdAndId(Long assignmentId, Long id);
}
