package com.bbte.styoudent.api.controller.assignment;

import com.bbte.styoudent.api.assembler.AssignmentAssembler;
import com.bbte.styoudent.api.exception.BadRequestException;
import com.bbte.styoudent.api.exception.NotFoundException;
import com.bbte.styoudent.api.util.AssignmentUtil;
import com.bbte.styoudent.api.util.ParticipationUtil;
import com.bbte.styoudent.dto.incoming.assignment.AssignmentCreationDto;
import com.bbte.styoudent.dto.outgoing.assignment.AssignmentDto;
import com.bbte.styoudent.model.assignment.Assignment;
import com.bbte.styoudent.model.course.Course;
import com.bbte.styoudent.model.person.Person;
import com.bbte.styoudent.model.person.Role;
import com.bbte.styoudent.security.util.AuthUtil;
import com.bbte.styoudent.service.assignment.AssignmentService;
import com.bbte.styoudent.service.course.CourseService;
import com.bbte.styoudent.service.person.PersonService;
import com.bbte.styoudent.service.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.stream.Collectors;

@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/courses/{courseId}/assignments")
public class AssignmentController {
    private final CourseService courseService;
    private final PersonService personService;
    private final AssignmentAssembler assignmentAssembler;
    private final AssignmentService assignmentService;
    private final ParticipationUtil participationUtil;
    private final AssignmentUtil assignmentUtil;

    public AssignmentController(CourseService courseService, PersonService personService,
                                AssignmentAssembler assignmentAssembler,
                                AssignmentService assignmentService, ParticipationUtil participationUtil,
                                AssignmentUtil assignmentUtil) {
        this.courseService = courseService;
        this.personService = personService;
        this.assignmentAssembler = assignmentAssembler;
        this.assignmentService = assignmentService;
        this.participationUtil = participationUtil;
        this.assignmentUtil = assignmentUtil;
    }

    @GetMapping("{assignmentId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER')")
    public ResponseEntity<AssignmentDto> getAssignment(@PathVariable(name = "courseId") Long courseId,
                                                       @PathVariable(name = "assignmentId") Long assignmentId) {
        log.debug("GET /courses/{}/assignments/{}", courseId, assignmentId);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());
        participationUtil.checkIfParticipates(courseId, person);

        try {
            Assignment assignment = assignmentService.getByCourseIdAndId(courseId, assignmentId);

            if (person.getRole().equals(Role.ROLE_STUDENT)) {
                assignmentUtil.checkIfPublished(courseId, assignmentId);

                assignment.setComments(assignment.getComments().stream().filter(
                        assignmentComment -> assignmentComment.getRecipient().equals(person)
                ).collect(Collectors.toList()));
            }

            return ResponseEntity.ok(assignmentAssembler.modelToDto(assignment));
        } catch (ServiceException se) {
            throw new NotFoundException("Assignment with id:  " + assignmentId + " doesn't exists!", se);
        }
    }

    @PostMapping()
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<AssignmentDto> saveAssignment(
            @PathVariable(name = "courseId") Long courseId,
            @RequestBody @Valid AssignmentCreationDto assignmentCreationDto) {
        log.debug("POST /courses/{}/assignments {}", courseId, assignmentCreationDto);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());
        participationUtil.checkIfParticipates(courseId, person);

        try {
            Course course = courseService.getById(courseId);

            Assignment assignment = assignmentAssembler.creationDtoToModel(assignmentCreationDto);
            assignment.setCourse(course);

            assignment = assignmentService.save(assignment);

            assignmentUtil.createMultipleNotificationsOfAssignmentCreation(assignment);

            return ResponseEntity.ok(assignmentAssembler.modelToDto(assignment));
        } catch (ServiceException se) {
            throw new BadRequestException("Could not POST assignment!", se);
        }
    }

    @PutMapping("{assignmentId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<AssignmentDto> updateAssignment(
            @PathVariable(name = "courseId") Long courseId,
            @PathVariable(name = "assignmentId") Long assignmentId,
            @RequestBody @Valid AssignmentCreationDto assignmentCreationDto) {
        log.debug("PUT /courses/{}/assignments/ {}", courseId, assignmentCreationDto);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());
        participationUtil.checkIfParticipates(courseId, person);

        try {
            Assignment assignment = assignmentService.getByCourseIdAndId(courseId, assignmentId);

            assignment.setName(assignmentCreationDto.getName());
            assignment.setDueDate(assignmentCreationDto.getDueDate());
            assignment.setPoints(assignmentCreationDto.getPoints());
            assignment.setDescription(assignmentCreationDto.getDescription());
            assignment.setPublished(assignmentCreationDto.getPublished());

            assignment = assignmentService.save(assignment);

            assignmentUtil.createMultipleNotificationsOfAssignmentCreation(assignment);

            return ResponseEntity.ok(assignmentAssembler.modelToDto(assignment));
        } catch (ServiceException se) {
            throw new BadRequestException("Could not PUT assignment!", se);
        }
    }

    @DeleteMapping("{assignmentId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> deleteAssignment(@PathVariable(name = "courseId") Long courseId,
                                              @PathVariable(name = "assignmentId") Long assignmentId) {
        log.debug("DELETE /courses/{}/assignments/{}", courseId, assignmentId);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());
        participationUtil.checkIfParticipates(courseId, person);
        assignmentUtil.checkIfHasThisAssignment(courseId, assignmentId);

        try {
            assignmentService.delete(assignmentId);

            return ResponseEntity.noContent().build();
        } catch (ServiceException se) {
            throw new BadRequestException("Could not DELETE assignment!", se);
        }
    }
}
