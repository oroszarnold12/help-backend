package com.help.repository.assignment;

import com.help.model.assignment.AssignmentGrade;
import com.help.model.person.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssignmentGradeRepository extends JpaRepository<AssignmentGrade, Long> {
    List<AssignmentGrade> findByAssignmentIdAndSubmitter(Long assignmentId, Person submitter);

    List<AssignmentGrade> findByAssignmentId(Long assignmentId);

    boolean existsByAssignmentIdAndSubmitter(Long assignmentId, Person submitter);
}
