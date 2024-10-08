package com.help.repository.assignment;

import com.help.model.assignment.AssignmentComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AssignmentCommentRepository extends JpaRepository<AssignmentComment, Long> {
    Optional<AssignmentComment> findByAssignmentIdAndId(Long assignmentId, Long id);
}
