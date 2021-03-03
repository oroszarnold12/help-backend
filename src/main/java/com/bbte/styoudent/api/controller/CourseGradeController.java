package com.bbte.styoudent.api.controller;

import com.bbte.styoudent.api.assembler.GradeAssembler;
import com.bbte.styoudent.api.exception.InternalServerException;
import com.bbte.styoudent.dto.outgoing.GradeDto;
import com.bbte.styoudent.model.Person;
import com.bbte.styoudent.security.util.AuthUtil;
import com.bbte.styoudent.service.AssignmentService;
import com.bbte.styoudent.service.PersonService;
import com.bbte.styoudent.service.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/courses/{courseId}/grades")
public class CourseGradeController {
    private final PersonService personService;
    private final GradeAssembler gradeAssembler;
    private final AssignmentService assignmentService;

    public CourseGradeController(PersonService personService, GradeAssembler gradeAssembler,
                                 AssignmentService assignmentService) {
        this.personService = personService;
        this.gradeAssembler = gradeAssembler;
        this.assignmentService = assignmentService;
    }

    @GetMapping()
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER')")
    public ResponseEntity<List<GradeDto>> getGrades(
            @PathVariable(name = "courseId") Long courseId
    ) {
        log.debug("GET /courses/{}/grades/", courseId);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());

        try {
            List<GradeDto> grades = new ArrayList<>();

            assignmentService.getByCourseId(courseId).forEach(assignment -> {
                assignment.getGrades().forEach(grade -> {
                    if (grade.getSubmitter().equals(person)) {
                        grades.add(gradeAssembler.modelToDto(grade));
                    }
                });
            });

            return ResponseEntity.ok(grades);
        } catch (ServiceException se) {
            throw new InternalServerException("Could not GET grades!", se);
        }
    }
}
