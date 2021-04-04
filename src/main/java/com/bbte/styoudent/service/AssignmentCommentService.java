package com.bbte.styoudent.service;

import com.bbte.styoudent.model.AssignmentComment;

public interface AssignmentCommentService {
    AssignmentComment save(AssignmentComment assignmentComment);

    AssignmentComment getByAssignmentIdAndId(Long assignmentId, Long id);

    void deleteById(Long id);
}
