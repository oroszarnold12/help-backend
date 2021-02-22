package com.bbte.styoudent.repository;

import com.bbte.styoudent.model.Grade;
import com.bbte.styoudent.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GradeRepository extends JpaRepository<Grade, Long> {
    List<Grade> findByAssignmentIdAndSubmitter(Long assignmentId, Person submitter);

    List<Grade> findByAssignmentId(Long assignmentId);

    boolean existsByAssignmentIdAndSubmitter(Long assignmentId, Person submitter);

    void deleteByAssignmentIdAndSubmitterId(Long assignmentId, Long submitterId);
}
