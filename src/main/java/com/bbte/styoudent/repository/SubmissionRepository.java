package com.bbte.styoudent.repository;

import com.bbte.styoudent.model.Person;
import com.bbte.styoudent.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findByAssignmentIdAndSubmitter(Long assignmentId, Person submitter);

    List<Submission> findByAssignmentId(Long assignmentId);

    Optional<Submission> findByAssignmentIdAndId(Long assignmentId, Long id);
}
