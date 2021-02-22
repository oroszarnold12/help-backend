package com.bbte.styoudent.service.impl;

import com.bbte.styoudent.model.Grade;
import com.bbte.styoudent.model.Person;
import com.bbte.styoudent.repository.GradeRepository;
import com.bbte.styoudent.service.GradeService;
import com.bbte.styoudent.service.ServiceException;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GradeServiceImpl implements GradeService {
    private final GradeRepository gradeRepository;

    public GradeServiceImpl(GradeRepository gradeRepository) {
        this.gradeRepository = gradeRepository;
    }

    @Override
    public List<Grade> getByAssignmentIdAndBySubmitter(Long assignmentId, Person submitter) {
        try {
            return gradeRepository.findByAssignmentIdAndSubmitter(assignmentId, submitter);
        } catch (DataAccessException de) {
            throw new ServiceException("Grade selection failed!", de);
        }
    }

    @Override
    public List<Grade> getByAssignmentId(Long assignmentId) {
        try {
            return gradeRepository.findByAssignmentId(assignmentId);
        } catch (DataAccessException de) {
            throw new ServiceException("Grade selection failed!", de);
        }
    }

    @Override
    public Grade save(Grade grade) {
        try {
            return gradeRepository.save(grade);
        } catch (DataAccessException de) {
            throw new ServiceException("Grade insertion failed!", de);
        }
    }

    @Override
    public boolean checkIfExistsByAssignmentIdAndSubmitter(Long assignmentId, Person submitter) {
        try {
            return gradeRepository.existsByAssignmentIdAndSubmitter(assignmentId, submitter);
        } catch (DataAccessException de) {
            throw new ServiceException("Grade checking failed!", de);
        }
    }

    @Transactional
    @Override
    public void deleteByAssignmentIdAndSubmitterId(Long assignmentId, Long submitterId) {
        try {
            gradeRepository.deleteByAssignmentIdAndSubmitterId(assignmentId, submitterId);
        } catch (DataAccessException de) {
            throw new ServiceException("Grade deletion failed!", de);
        }
    }
}
