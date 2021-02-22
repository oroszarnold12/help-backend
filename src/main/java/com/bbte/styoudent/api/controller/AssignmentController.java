package com.bbte.styoudent.api.controller;

import com.bbte.styoudent.api.assembler.AssignmentAssembler;
import com.bbte.styoudent.api.exception.BadRequestException;
import com.bbte.styoudent.api.exception.ForbiddenException;
import com.bbte.styoudent.api.exception.InternalServerException;
import com.bbte.styoudent.api.exception.NotFoundException;
import com.bbte.styoudent.dto.incoming.AssignmentCreationDto;
import com.bbte.styoudent.dto.outgoing.AssignmentDto;
import com.bbte.styoudent.model.Assignment;
import com.bbte.styoudent.model.Course;
import com.bbte.styoudent.model.Person;
import com.bbte.styoudent.security.util.AuthUtil;
import com.bbte.styoudent.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/courses/{courseId}/assignments")
public class AssignmentController {
    private final CourseService courseService;
    private final PersonService personService;
    private final AssignmentAssembler assignmentAssembler;
    private final ParticipationService participationService;
    private final AssignmentService assignmentService;

    public AssignmentController(CourseService courseService, PersonService personService,
                                AssignmentAssembler assignmentAssembler, ParticipationService participationService,
                                AssignmentService assignmentService) {
        this.courseService = courseService;
        this.personService = personService;
        this.assignmentAssembler = assignmentAssembler;
        this.participationService = participationService;
        this.assignmentService = assignmentService;
    }

    @GetMapping(value = "{assignmentId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER')")
    public ResponseEntity<AssignmentDto> getAssignment(@PathVariable(name = "courseId") Long courseId,
                                                       @PathVariable(name = "assignmentId") Long assignmentId) {
        log.debug("GET /courses/{}/assignments/{}", courseId, assignmentId);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());
        checkIfParticipates(courseId, person);

        try {
            Assignment assignment = assignmentService.getByCourseIdAndId(courseId, assignmentId);

            return ResponseEntity.ok(assignmentAssembler.modelToDto(assignment));
        } catch (ServiceException se) {
            throw new NotFoundException("Assignment with id:  " + courseId + " doesn't exists!", se);
        }
    }

    @PostMapping()
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<AssignmentDto> saveAssignment(
            @PathVariable(name = "courseId") Long courseId,
            @RequestBody @Valid AssignmentCreationDto assignmentCreationDto) {
        log.debug("POST /courses/{}/assignments {}", courseId, assignmentCreationDto);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());
        checkIfParticipates(courseId, person);

        try {
            Course course = courseService.getById(courseId);

            Assignment assignment = assignmentAssembler.creationDtoToModel(assignmentCreationDto);
            assignment.setCourse(course);

            return ResponseEntity.ok(assignmentAssembler.modelToDto(
                    assignmentService.save(assignment)
            ));
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
        checkIfParticipates(courseId, person);

        try {
            Assignment assignment = assignmentService.getByCourseIdAndId(courseId, assignmentId);

            assignment.setName(assignmentCreationDto.getName());
            assignment.setDueDate(assignmentCreationDto.getDueDate());
            assignment.setPoints(assignmentCreationDto.getPoints());
            assignment.setDescription(assignmentCreationDto.getDescription());

            assignmentService.save(assignment);

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
        checkIfParticipates(courseId, person);
        checkIfHasThisAssignment(courseId, assignmentId);

        try {
            assignmentService.delete(assignmentId);

            return ResponseEntity.noContent().build();
        } catch (ServiceException se) {
            throw new BadRequestException("Could not DELETE assignment!", se);
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
