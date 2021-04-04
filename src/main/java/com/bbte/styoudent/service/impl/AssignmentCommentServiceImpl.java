package com.bbte.styoudent.service.impl;

import com.bbte.styoudent.model.AssignmentComment;
import com.bbte.styoudent.repository.AssignmentCommentRepository;
import com.bbte.styoudent.service.AssignmentCommentService;
import com.bbte.styoudent.service.ServiceException;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
public class AssignmentCommentServiceImpl implements AssignmentCommentService {
    private final AssignmentCommentRepository assignmentCommentRepository;

    public AssignmentCommentServiceImpl(AssignmentCommentRepository assignmentCommentRepository) {
        this.assignmentCommentRepository = assignmentCommentRepository;
    }

    @Override
    public AssignmentComment save(AssignmentComment assignmentComment) {
        try {
            return assignmentCommentRepository.save(assignmentComment);
        } catch (DataAccessException de) {
            throw new ServiceException("Assignment comment insertion failed!", de);
        }
    }

    @Override
    public AssignmentComment getByAssignmentIdAndId(Long assignmentId, Long id) {
        return assignmentCommentRepository.findByAssignmentIdAndId(assignmentId, id).orElseThrow(
                () -> new ServiceException("Assignment comment selection with id: " + id + " failed!")
        );
    }

    @Override
    public void deleteById(Long id) {
        try {
            assignmentCommentRepository.deleteById(id);
        } catch (DataAccessException de) {
            throw new ServiceException("Assignment comment deletion with id:" + id + "failed!", de);
        }
    }
}
