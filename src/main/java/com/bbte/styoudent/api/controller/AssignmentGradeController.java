package com.bbte.styoudent.api.controller;

import com.bbte.styoudent.api.assembler.GradeAssembler;
import com.bbte.styoudent.api.exception.BadRequestException;
import com.bbte.styoudent.api.exception.ForbiddenException;
import com.bbte.styoudent.api.exception.InternalServerException;
import com.bbte.styoudent.dto.incoming.GradeCreationDto;
import com.bbte.styoudent.dto.outgoing.GradeDto;
import com.bbte.styoudent.model.Assignment;
import com.bbte.styoudent.model.Grade;
import com.bbte.styoudent.model.Person;
import com.bbte.styoudent.model.Role;
import com.bbte.styoudent.security.util.AuthUtil;
import com.bbte.styoudent.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/courses/{courseId}/assignments/{assignmentId}/grades")
public class AssignmentGradeController {
    private final PersonService personService;
    private final GradeAssembler gradeAssembler;
    private final ParticipationService participationService;
    private final GradeService gradeService;
    private final AssignmentService assignmentService;

    public AssignmentGradeController(PersonService personService,
                                     GradeAssembler gradeAssembler, ParticipationService participationService,
                                     GradeService gradeService, AssignmentService assignmentService) {
        this.personService = personService;
        this.gradeAssembler = gradeAssembler;
        this.participationService = participationService;
        this.gradeService = gradeService;
        this.assignmentService = assignmentService;
    }

    @GetMapping()
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER')")
    public ResponseEntity<List<GradeDto>> getGrades(
            @PathVariable(name = "courseId") Long courseId,
            @PathVariable(name = "assignmentId") Long assignmentId
    ) {
        log.debug("GET /courses/{}/assignments/{}/grades", courseId, assignmentId);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());
        checkIfParticipates(courseId, person);
        checkIfHasThisAssignment(courseId, assignmentId);

        try {
            if (person.getRole().equals(Role.ROLE_STUDENT)) {
                return ResponseEntity.ok(gradeService.getByAssignmentIdAndBySubmitter(assignmentId, person).stream()
                        .map(gradeAssembler::modelToDto)
                        .collect(Collectors.toList()));
            } else {
                return ResponseEntity.ok(gradeService.getByAssignmentId(assignmentId).stream()
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
        checkIfParticipates(courseId, person);

        try {
            Assignment assignment = assignmentService.getByCourseIdAndId(courseId, assignmentId);

            Person submitter = personService.getPersonById(gradeCreationDto.getPersonId());

            Grade grade;

            if (checkIfGraded(assignment.getId(), submitter)) {
                grade = gradeService.getByAssignmentIdAndBySubmitter(assignmentId, submitter).get(0);
                grade.setGrade(gradeCreationDto.getGrade());
            } else {
                grade = new Grade();
                grade.setGrade(gradeCreationDto.getGrade());
                grade.setAssignment(assignment);
                grade.setSubmitter(submitter);
            }

            return ResponseEntity.ok(gradeAssembler.modelToDto(
                    gradeService.save(grade)
            ));
        } catch (ServiceException se) {
            throw new InternalServerException("Could not POST grade!", se);
        }
    }

    private void checkIfParticipates(Long courseId, Person person) {
        try {
            if (!participationService.checkIfParticipates(courseId, person)) {
                throw new ForbiddenException("Access denied!");
            }
        } catch (ServiceException se) {
            throw new InternalServerException("Could not check participation!", se);
        }
    }

    private boolean checkIfGraded(Long assignmentId, Person submitter) {
        try {
            return gradeService.checkIfExistsByAssignmentIdAndSubmitter(assignmentId, submitter);
        } catch (ServiceException se) {
            throw new InternalServerException("Could not check grade!", se);
        }
    }

    private void checkIfHasThisAssignment(Long courseId, Long assignmentId) {
        try {
            if (!assignmentService.checkIfExistsByCourseIdAndId(courseId, assignmentId)) {
                throw new BadRequestException(
                        "Course with id: " + courseId + " has no assignment with id: " + assignmentId + "!"
                );
            }
        } catch (ServiceException se) {
            throw new InternalServerException("Could not check assignment!", se);
        }
    }
}
