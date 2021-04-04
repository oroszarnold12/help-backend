package com.bbte.styoudent.service.impl;

import com.bbte.styoudent.model.AssignmentGrade;
import com.bbte.styoudent.model.Person;
import com.bbte.styoudent.repository.AssignmentGradeRepository;
import com.bbte.styoudent.service.AssignmentGradeService;
import com.bbte.styoudent.service.ServiceException;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AssignmentGradeServiceImpl implements AssignmentGradeService {
    private final AssignmentGradeRepository assignmentGradeRepository;

    public AssignmentGradeServiceImpl(AssignmentGradeRepository assignmentGradeRepository) {
        this.assignmentGradeRepository = assignmentGradeRepository;
    }

    @Override
    public List<AssignmentGrade> getByAssignmentIdAndBySubmitter(Long assignmentId, Person submitter) {
        try {
            return assignmentGradeRepository.findByAssignmentIdAndSubmitter(assignmentId, submitter);
        } catch (DataAccessException de) {
            throw new ServiceException("Assignment grade selection failed!", de);
        }
    }

    @Override
    public List<AssignmentGrade> getByAssignmentId(Long assignmentId) {
        try {
            return assignmentGradeRepository.findByAssignmentId(assignmentId);
        } catch (DataAccessException de) {
            throw new ServiceException("Assignment grade selection failed!", de);
        }
    }

    @Override
    public AssignmentGrade save(AssignmentGrade assignmentGrade) {
        try {
            return assignmentGradeRepository.save(assignmentGrade);
        } catch (DataAccessException de) {
            throw new ServiceException("Assignment grade insertion failed!", de);
        }
    }

    @Override
    public boolean checkIfExistsByAssignmentIdAndSubmitter(Long assignmentId, Person submitter) {
        try {
            return assignmentGradeRepository.existsByAssignmentIdAndSubmitter(assignmentId, submitter);
        } catch (DataAccessException de) {
            throw new ServiceException("Assignment grade checking failed!", de);
        }
    }
}
