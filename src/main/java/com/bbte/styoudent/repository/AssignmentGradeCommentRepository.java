package com.bbte.styoudent.repository;

import com.bbte.styoudent.model.AssignmentGradeComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AssignmentGradeCommentRepository extends JpaRepository<AssignmentGradeComment, Long> {
    Optional<AssignmentGradeComment> findByAssignmentGradeIdAndId(Long assignmentGradeId, Long id);
}
