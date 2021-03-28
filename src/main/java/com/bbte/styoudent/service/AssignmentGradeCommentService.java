package com.bbte.styoudent.service;

import com.bbte.styoudent.model.AssignmentGradeComment;

public interface AssignmentGradeCommentService {
    AssignmentGradeComment save(AssignmentGradeComment assignmentGradeComment);

    AssignmentGradeComment getByAssignmentGradeIdAndId(Long assignmentGradeId, Long id);

    void deleteById(Long id);
}
