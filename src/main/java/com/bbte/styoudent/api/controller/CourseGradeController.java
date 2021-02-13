package com.bbte.styoudent.api.controller;

import com.bbte.styoudent.api.assembler.GradeAssembler;
import com.bbte.styoudent.api.exception.InternalServerException;
import com.bbte.styoudent.dto.outgoing.GradeDto;
import com.bbte.styoudent.model.Course;
import com.bbte.styoudent.model.Person;
import com.bbte.styoudent.security.util.AuthUtil;
import com.bbte.styoudent.service.CourseService;
import com.bbte.styoudent.service.PersonService;
import com.bbte.styoudent.service.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/courses/{courseId}/grades")
public class CourseGradeController {
    private final PersonService personService;
    private final CourseService courseService;
    private final GradeAssembler gradeAssembler;

    public CourseGradeController(PersonService personService, CourseService courseService,
                                 GradeAssembler gradeAssembler) {
        this.personService = personService;
        this.courseService = courseService;
        this.gradeAssembler = gradeAssembler;
    }

    @GetMapping()
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER')")
    public ResponseEntity<List<GradeDto>> getGrades(
            @PathVariable(name = "courseId") Long courseId
    ) {
        log.debug("GET /courses/{}/grades/", courseId);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());

        try {
            Course course = courseService.getById(courseId);

            List<GradeDto> grades = new ArrayList<>();

            course.getAssignments().forEach(assignment -> {
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
