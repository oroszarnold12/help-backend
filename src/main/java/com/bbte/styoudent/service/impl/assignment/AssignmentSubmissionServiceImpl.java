package com.bbte.styoudent.service.impl.assignment;

import com.bbte.styoudent.model.person.Person;
import com.bbte.styoudent.model.assignment.AssignmentSubmission;
import com.bbte.styoudent.repository.assignment.AssignmentSubmissionRepository;
import com.bbte.styoudent.service.ServiceException;
import com.bbte.styoudent.service.assignment.AssignmentSubmissionService;
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
