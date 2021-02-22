package com.bbte.styoudent.service.impl;

import com.bbte.styoudent.model.Person;
import com.bbte.styoudent.model.Submission;
import com.bbte.styoudent.repository.SubmissionRepository;
import com.bbte.styoudent.service.ServiceException;
import com.bbte.styoudent.service.SubmissionService;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubmissionServiceImpl implements SubmissionService {
    private final SubmissionRepository submissionRepository;

    public SubmissionServiceImpl(SubmissionRepository submissionRepository) {
        this.submissionRepository = submissionRepository;
    }

    @Override
    public List<Submission> getByAssignmentIdAndBySubmitter(Long assignmentId, Person submitter) {
        try {
            return submissionRepository.findByAssignmentIdAndSubmitter(assignmentId, submitter);
        } catch (DataAccessException de) {
            throw new ServiceException("Submission selection failed!", de);
        }
    }

    @Override
    public List<Submission> getByAssignmentId(Long assignmentId) {
        try {
            return submissionRepository.findByAssignmentId(assignmentId);
        } catch (DataAccessException de) {
            throw new ServiceException("Submission selection failed!", de);
        }
    }

    @Override
    public Submission getByAssignmentIdAndId(Long assignmentId, Long id) {
        return submissionRepository.findByAssignmentIdAndId(assignmentId, id).orElseThrow(() ->
                new ServiceException("Submission selection with id: " + id + " failed!")
        );
    }

    @Override
    public Submission save(Submission submission) {
        try {
            return submissionRepository.save(submission);
        } catch (DataAccessException de) {
            throw new ServiceException("Submission insertion failed!", de);
        }
    }
}
