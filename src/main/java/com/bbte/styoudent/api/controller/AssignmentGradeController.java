package com.bbte.styoudent.api.controller;

import com.bbte.styoudent.api.assembler.GradeAssembler;
import com.bbte.styoudent.api.exception.InternalServerException;
import com.bbte.styoudent.api.util.AssignmentUtil;
import com.bbte.styoudent.api.util.ParticipationUtil;
import com.bbte.styoudent.dto.incoming.GradeCreationDto;
import com.bbte.styoudent.dto.outgoing.GradeDto;
import com.bbte.styoudent.model.Assignment;
import com.bbte.styoudent.model.AssignmentGrade;
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
    private final AssignmentGradeService assignmentGradeService;
    private final AssignmentService assignmentService;
    private final ParticipationUtil participationUtil;
    private final AssignmentUtil assignmentUtil;

    public AssignmentGradeController(PersonService personService,
                                     GradeAssembler gradeAssembler,
                                     AssignmentGradeService assignmentGradeService, AssignmentService assignmentService,
                                     ParticipationUtil participationUtil, AssignmentUtil assignmentUtil) {
        this.personService = personService;
        this.gradeAssembler = gradeAssembler;
        this.assignmentGradeService = assignmentGradeService;
        this.assignmentService = assignmentService;
        this.participationUtil = participationUtil;
        this.assignmentUtil = assignmentUtil;
    }

    @GetMapping()
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER')")
    public ResponseEntity<List<GradeDto>> getGrades(
            @PathVariable(name = "courseId") Long courseId,
            @PathVariable(name = "assignmentId") Long assignmentId
    ) {
        log.debug("GET /courses/{}/assignments/{}/grades", courseId, assignmentId);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());
        participationUtil.checkIfParticipates(courseId, person);
        assignmentUtil.checkIfHasThisAssignment(courseId, assignmentId);

        try {
            if (person.getRole().equals(Role.ROLE_STUDENT)) {
                return ResponseEntity.ok(assignmentGradeService.getByAssignmentIdAndBySubmitter(assignmentId, person).stream()
                        .map(gradeAssembler::modelToDto)
                        .collect(Collectors.toList()));
            } else {
                return ResponseEntity.ok(assignmentGradeService.getByAssignmentId(assignmentId).stream()
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
        participationUtil.checkIfParticipates(courseId, person);

        try {
            Assignment assignment = assignmentService.getByCourseIdAndId(courseId, assignmentId);

            Person submitter = personService.getPersonById(gradeCreationDto.getPersonId());

            AssignmentGrade assignmentGrade;

            if (assignmentUtil.checkIfGraded(assignment.getId(), submitter)) {
                assignmentGrade = assignmentGradeService.getByAssignmentIdAndBySubmitter(assignmentId, submitter).get(0);
                assignmentGrade.setGrade(gradeCreationDto.getGrade());
            } else {
                assignmentGrade = new AssignmentGrade();
                assignmentGrade.setGrade(gradeCreationDto.getGrade());
                assignmentGrade.setAssignment(assignment);
                assignmentGrade.setSubmitter(submitter);
            }

            return ResponseEntity.ok(gradeAssembler.modelToDto(
                    assignmentGradeService.save(assignmentGrade)
            ));
        } catch (ServiceException se) {
            throw new InternalServerException("Could not POST grade!", se);
        }
    }
}
