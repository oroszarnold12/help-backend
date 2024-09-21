package com.help.service.impl.assignment;

import com.help.model.person.Person;
import com.help.model.assignment.AssignmentSubmission;
import com.help.repository.assignment.AssignmentSubmissionRepository;
import com.help.service.ServiceException;
import com.help.service.assignment.AssignmentSubmissionService;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssignmentSubmissionServiceImpl implements AssignmentSubmissionService {
    private final AssignmentSubmissionRepository assignmentSubmissionRepository;

    public AssignmentSubmissionServiceImpl(AssignmentSubmissionRepository assignmentSubmissionRepository) {
        this.assignmentSubmissionRepository = assignmentSubmissionRepository;
    }

    @Override
    public List<AssignmentSubmission> getByAssignmentIdAndBySubmitter(Long assignmentId, Person submitter) {
        try {
            return assignmentSubmissionRepository.findByAssignmentIdAndSubmitter(assignmentId, submitter);
        } catch (DataAccessException de) {
            throw new ServiceException("Submission selection failed!", de);
        }
    }

    @Override
    public List<AssignmentSubmission> getByAssignmentId(Long assignmentId) {
        try {
            return assignmentSubmissionRepository.findByAssignmentId(assignmentId);
        } catch (DataAccessException de) {
            throw new ServiceException("Submission selection failed!", de);
        }
    }

    @Override
    public AssignmentSubmission getByAssignmentIdAndId(Long assignmentId, Long id) {
        return assignmentSubmissionRepository.findByAssignmentIdAndId(assignmentId, id).orElseThrow(() ->
                new ServiceException("Submission selection with id: " + id + " failed!")
        );
    }

    @Override
    public AssignmentSubmission save(AssignmentSubmission assignmentSubmission) {
        try {
            return assignmentSubmissionRepository.save(assignmentSubmission);
        } catch (DataAccessException de) {
            throw new ServiceException("Submission insertion failed!", de);
        }
    }
}
