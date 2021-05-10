package com.bbte.styoudent.service.assignment;

import com.bbte.styoudent.model.assignment.AssignmentComment;

public interface AssignmentCommentService {
    AssignmentComment save(AssignmentComment assignmentComment);

    AssignmentComment getByAssignmentIdAndId(Long assignmentId, Long id);

    void deleteById(Long id);
}
