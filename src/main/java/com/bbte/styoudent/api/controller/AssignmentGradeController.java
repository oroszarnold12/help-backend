package com.bbte.styoudent.api.controller;

import com.bbte.styoudent.api.assembler.GradeAssembler;
import com.bbte.styoudent.api.exception.ForbiddenException;
import com.bbte.styoudent.api.exception.InternalServerException;
import com.bbte.styoudent.api.exception.NotFoundException;
import com.bbte.styoudent.dto.incoming.GradeCreationDto;
import com.bbte.styoudent.dto.outgoing.GradeDto;
import com.bbte.styoudent.model.*;
import com.bbte.styoudent.security.util.AuthUtil;
import com.bbte.styoudent.service.CourseService;
import com.bbte.styoudent.service.ParticipationService;
import com.bbte.styoudent.service.PersonService;
import com.bbte.styoudent.service.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/courses/{courseId}/assignments/{assignmentId}/grades")
public class AssignmentGradeController {
    private final PersonService personService;
    private final CourseService courseService;
    private final GradeAssembler gradeAssembler;
    private final ParticipationService participationService;

    public AssignmentGradeController(PersonService personService, CourseService courseService,
                                     GradeAssembler gradeAssembler, ParticipationService participationService) {
        this.personService = personService;
        this.courseService = courseService;
        this.gradeAssembler = gradeAssembler;
        this.participationService = participationService;
    }

    @GetMapping()
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER')")
    public ResponseEntity<List<GradeDto>> getGrades(
            @PathVariable(name = "courseId") Long courseId,
            @PathVariable(name = "assignmentId") Long assignmentId
    ) {
        log.debug("GET /courses/{}/assignments/{}/grades", courseId, assignmentId);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());

        try {
            Course course = courseService.getById(courseId);

            checkIfParticipates(course, person);

            Assignment assignment = getAssignment(course, assignmentId);

            if (person.getRole().equals(Role.ROLE_STUDENT)) {
                return ResponseEntity.ok(assignment.getGrades().stream().filter((grade ->
                        grade.getSubmitter().equals(person)))
                        .map(gradeAssembler::modelToDto)
                        .collect(Collectors.toList()));
            } else {
                return ResponseEntity.ok(assignment.getGrades().stream()
                        .map(gradeAssembler::modelToDto)
                        .collect(Collectors.toList()));
            }
        } catch (ServiceException se) {
            throw new InternalServerException("Could not GET grades!", se);
        }
    }

    @PostMapping()
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<GradeDto> getGrades(
            @PathVariable(name = "courseId") Long courseId,
            @PathVariable(name = "assignmentId") Long assignmentId,
            @RequestBody @Valid GradeCreationDto gradeCreationDto
    ) {
        log.debug("POST /courses/{}/assignments/{}/grades", courseId, assignmentId);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());

        try {
            Course course = courseService.getById(courseId);

            checkIfParticipates(course, person);

            Assignment assignment = getAssignment(course, assignmentId);

            Person submitter = personService.getPersonById(gradeCreationDto.getPersonId());

            Grade grade;

            if (checkIfGraded(assignment, submitter)) {
                grade = getGrade(assignment, submitter);
                grade.setGrade(gradeCreationDto.getGrade());
            } else {
                grade = new Grade();
                grade.setGrade(gradeCreationDto.getGrade());
                grade.setAssignment(assignment);
                grade.setSubmitter(submitter);
                assignment.getGrades().add(grade);
            }

            courseService.save(course);

            return ResponseEntity.ok(gradeAssembler.modelToDto(grade));
        } catch (ServiceException se) {
            throw new InternalServerException("Could not POST grade!", se);
        }
    }

    private void checkIfParticipates(Course course, Person person) {
        try {
            if (!participationService.checkIfParticipates(course, person)) {
                throw new ForbiddenException("Access denied!");
            }
        } catch (ServiceException se) {
            throw new InternalServerException("Could not check participation!", se);
        }
    }

    private Assignment getAssignment(Course course, Long assignmentId) {
        return course.getAssignments().stream().filter((assignment1 ->
                assignment1.getId().equals(assignmentId))).findFirst().orElseThrow(() ->
                new NotFoundException("Assignment with id: " + assignmentId + " doesn't exists!"));
    }

    private boolean checkIfGraded(Assignment assignment, Person submitter) {
        List<Grade> grades = assignment.getGrades().stream().filter(grade -> {
            return grade.getSubmitter().equals(submitter);
        }).collect(Collectors.toList());

        return !grades.isEmpty();
    }

    private Grade getGrade(Assignment assignment, Person submitter) {
        List<Grade> grades = assignment.getGrades().stream().filter(grade -> {
            return grade.getSubmitter().equals(submitter);
        }).collect(Collectors.toList());

        return grades.get(0);
    }
}
