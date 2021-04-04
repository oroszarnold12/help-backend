package com.bbte.styoudent.repository;

import com.bbte.styoudent.model.AssignmentComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AssignmentCommentRepository extends JpaRepository<AssignmentComment, Long> {
    Optional<AssignmentComment> findByAssignmentIdAndId(Long assignmentId, Long id);
}
