package com.help.service.impl.assignment;

import com.help.model.assignment.AssignmentComment;
import com.help.repository.assignment.AssignmentCommentRepository;
import com.help.service.assignment.AssignmentCommentService;
import com.help.service.ServiceException;
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
