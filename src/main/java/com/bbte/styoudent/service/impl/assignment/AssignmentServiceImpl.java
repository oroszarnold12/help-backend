package com.bbte.styoudent.service.impl.assignment;

import com.bbte.styoudent.model.assignment.Assignment;
import com.bbte.styoudent.repository.assignment.AssignmentRepository;
import com.bbte.styoudent.service.assignment.AssignmentService;
import com.bbte.styoudent.service.ServiceException;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssignmentServiceImpl implements AssignmentService {
    private final AssignmentRepository assignmentRepository;

    public AssignmentServiceImpl(AssignmentRepository assignmentRepository) {
        this.assignmentRepository = assignmentRepository;
    }

    @Override
    public Assignment getByCourseIdAndId(Long courseId, Long id) {
        return assignmentRepository.findByCourseIdAndId(courseId, id).orElseThrow(() ->
                new ServiceException("Assignment selection with id: " + id + " failed!"));
    }

    @Override
    public Assignment save(Assignment assignment) {
        try {
            return assignmentRepository.save(assignment);
        } catch (DataAccessException de) {
            throw new ServiceException("Assignment insertion failed!", de);
        }
    }

    @Override
    public void delete(Long id) {
        try {
            assignmentRepository.deleteById(id);
        } catch (DataAccessException de) {
            throw new ServiceException("Assignment deletion with id: " + id + " failed!", de);
        }
    }

    @Override
    public boolean checkIfExistsByCourseIdAndId(Long courseId, Long id) {
        try {
            return assignmentRepository.existsByCourseIdAndId(courseId, id);
        } catch (DataAccessException de) {
            throw new ServiceException("Assignment checking failed!", de);
        }
    }

    @Override
    public List<Assignment> getByCourseId(Long courseId) {
        try {
            return assignmentRepository.findByCourseId(courseId);
        } catch (DataAccessException de) {
            throw new ServiceException("Assignment selection failed!", de);
        }
    }
}
