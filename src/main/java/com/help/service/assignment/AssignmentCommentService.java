package com.help.service.assignment;

import com.help.model.assignment.AssignmentComment;

public interface AssignmentCommentService {
    AssignmentComment save(AssignmentComment assignmentComment);

    AssignmentComment getByAssignmentIdAndId(Long assignmentId, Long id);

    void deleteById(Long id);
}
