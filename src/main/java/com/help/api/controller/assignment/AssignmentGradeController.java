package com.help.api.controller.assignment;

import com.help.api.assembler.GradeAssembler;
import com.help.api.exception.InternalServerException;
import com.help.api.util.AssignmentUtil;
import com.help.api.util.ParticipationUtil;
import com.help.dto.incoming.assignment.GradeCreationDto;
import com.help.dto.outgoing.assignment.AssignmentGradeDto;
import com.help.model.assignment.Assignment;
import com.help.model.assignment.AssignmentGrade;
import com.help.model.person.Person;
import com.help.model.person.Role;
import com.help.security.util.AuthUtil;
import com.help.service.assignment.AssignmentGradeService;
import com.help.service.assignment.AssignmentService;
import com.help.service.person.PersonService;
import com.help.service.ServiceException;
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
    public ResponseEntity<List<AssignmentGradeDto>> getGrades(
            @PathVariable(name = "courseId") Long courseId,
            @PathVariable(name = "assignmentId") Long assignmentId
    ) {
        log.debug("GET /courses/{}/assignments/{}/grades", courseId, assignmentId);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());
        participationUtil.checkIfParticipates(courseId, person);
        assignmentUtil.checkIfHasThisAssignment(courseId, assignmentId);

        try {
            if (person.getRole().equals(Role.ROLE_STUDENT)) {
                return ResponseEntity.ok(assignmentGradeService.getByAssignmentIdAndBySubmitter(assignmentId, person)
                        .stream().map(gradeAssembler::modelToDto)
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
    public ResponseEntity<AssignmentGradeDto> getGrades(
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
                assignmentGrade = assignmentGradeService.getByAssignmentIdAndBySubmitter(assignmentId, submitter)
                        .get(0);
                assignmentGrade.setGrade(gradeCreationDto.getGrade());
            } else {
                assignmentGrade = new AssignmentGrade();
                assignmentGrade.setGrade(gradeCreationDto.getGrade());
                assignmentGrade.setAssignment(assignment);
                assignmentGrade.setSubmitter(submitter);
            }

            assignmentGrade = assignmentGradeService.save(assignmentGrade);

            assignmentUtil.createSingleNotificationOfAssignmentGraded(assignmentGrade, submitter);

            return ResponseEntity.ok(gradeAssembler.modelToDto(assignmentGrade));
        } catch (ServiceException se) {
            throw new InternalServerException("Could not POST grade!", se);
        }
    }
}
