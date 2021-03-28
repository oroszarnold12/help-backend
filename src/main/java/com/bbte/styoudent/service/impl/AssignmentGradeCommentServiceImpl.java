package com.bbte.styoudent.service.impl;

import com.bbte.styoudent.model.AssignmentGradeComment;
import com.bbte.styoudent.repository.AssignmentGradeCommentRepository;
import com.bbte.styoudent.service.AssignmentGradeCommentService;
import com.bbte.styoudent.service.ServiceException;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
public class AssignmentGradeCommentServiceImpl implements AssignmentGradeCommentService {
    private final AssignmentGradeCommentRepository assignmentGradeCommentRepository;

    public AssignmentGradeCommentServiceImpl(AssignmentGradeCommentRepository assignmentGradeCommentRepository) {
        this.assignmentGradeCommentRepository = assignmentGradeCommentRepository;
    }

    @Override
    public AssignmentGradeComment save(AssignmentGradeComment assignmentGradeComment) {
        try {
            return assignmentGradeCommentRepository.save(assignmentGradeComment);
        } catch (DataAccessException de) {
            throw new ServiceException("Assignment grade comment insertion failed!", de);
        }
    }

    @Override
    public AssignmentGradeComment getByAssignmentGradeIdAndId(Long assignmentGradeId, Long id) {
        return assignmentGradeCommentRepository.findByAssignmentGradeIdAndId(assignmentGradeId, id).orElseThrow(
                () -> new ServiceException("Assignment grade comment selection with id: " + id + " failed!")
        );
    }

    @Override
    public void deleteById(Long id) {
        try {
            assignmentGradeCommentRepository.deleteById(id);
        } catch (DataAccessException de) {
            throw new ServiceException("Assignment grade comment deletion with id:" + id + "failed!", de);
        }
    }
}
