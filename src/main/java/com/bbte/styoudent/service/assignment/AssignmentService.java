package com.bbte.styoudent.service.assignment;

import com.bbte.styoudent.model.assignment.Assignment;

import java.util.List;

public interface AssignmentService {
    Assignment getByCourseIdAndId(Long courseId, Long id);

    Assignment save(Assignment assignment);

    void delete(Long id);

    boolean checkIfExistsByCourseIdAndId(Long courseId, Long id);

    List<Assignment> getByCourseId(Long courseId);
}
