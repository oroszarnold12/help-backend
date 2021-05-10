package com.bbte.styoudent.repository.assignment;

import com.bbte.styoudent.model.assignment.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    Optional<Assignment> findByCourseIdAndId(Long courseId, Long id);

    boolean existsByCourseIdAndId(Long courseId, Long id);

    List<Assignment> findByCourseId(Long courseId);
}
