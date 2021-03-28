package com.bbte.styoudent.repository;

import com.bbte.styoudent.model.AssignmentGrade;
import com.bbte.styoudent.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssignmentGradeRepository extends JpaRepository<AssignmentGrade, Long> {
    List<AssignmentGrade> findByAssignmentIdAndSubmitter(Long assignmentId, Person submitter);

    List<AssignmentGrade> findByAssignmentId(Long assignmentId);

    Optional<AssignmentGrade> findByAssignmentIdAndId(Long assignmentId, Long id);

    boolean existsByAssignmentIdAndSubmitter(Long assignmentId, Person submitter);

    boolean existsByAssignmentIdAndId(Long assignmentId, Long id);

    void deleteByAssignmentIdAndSubmitterId(Long assignmentId, Long submitterId);
}
