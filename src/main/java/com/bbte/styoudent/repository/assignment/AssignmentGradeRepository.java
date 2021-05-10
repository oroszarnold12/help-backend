package com.bbte.styoudent.repository.assignment;

import com.bbte.styoudent.model.assignment.AssignmentGrade;
import com.bbte.styoudent.model.person.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssignmentGradeRepository extends JpaRepository<AssignmentGrade, Long> {
    List<AssignmentGrade> findByAssignmentIdAndSubmitter(Long assignmentId, Person submitter);

    List<AssignmentGrade> findByAssignmentId(Long assignmentId);

    boolean existsByAssignmentIdAndSubmitter(Long assignmentId, Person submitter);
}
