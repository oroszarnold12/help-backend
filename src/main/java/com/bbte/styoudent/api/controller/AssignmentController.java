package com.bbte.styoudent.api.controller;

import com.bbte.styoudent.api.assembler.AssignmentAssembler;
import com.bbte.styoudent.api.exception.BadRequestException;
import com.bbte.styoudent.api.exception.NotFoundException;
import com.bbte.styoudent.dto.incoming.AssignmentCreationDto;
import com.bbte.styoudent.dto.outgoing.AssignmentDto;
import com.bbte.styoudent.model.Assignment;
import com.bbte.styoudent.model.Course;
import com.bbte.styoudent.model.Person;
import com.bbte.styoudent.security.util.AuthUtil;
import com.bbte.styoudent.service.CourseService;
import com.bbte.styoudent.service.PersonService;
import com.bbte.styoudent.service.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/courses/{courseId}/assignments")
public class AssignmentController {
    private final CourseService courseService;
    private final PersonService personService;
    private final AssignmentAssembler assignmentAssembler;

    public AssignmentController(CourseService courseService, PersonService personService,
                                AssignmentAssembler assignmentAssembler) {
        this.courseService = courseService;
        this.personService = personService;
        this.assignmentAssembler = assignmentAssembler;
    }

    @GetMapping(value = "{assignmentId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER')")
    public ResponseEntity<AssignmentDto> getAssignment(@PathVariable(name = "courseId") Long courseId,
                                                       @PathVariable(name = "assignmentId") Long assignmentId) {
        log.debug("GET /courses/{}/assignments/{}", courseId, assignmentId);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());

        try {
            Course course = courseService.getCourseByPerson(person, courseId);

            Assignment assignment = course.getAssignments().stream().filter((assignment1 ->
                    assignment1.getId().equals(assignmentId))).findFirst().orElseThrow(() ->
                    new NotFoundException("Assignment with id: " + assignmentId + " doesn't exists!"));

            return ResponseEntity.ok(assignmentAssembler.modelToDto(assignment));
        } catch (ServiceException se) {
            throw new NotFoundException("Course with id:  " + courseId + " doesn't exists!", se);
        }
    }

    @PostMapping()
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<AssignmentDto> saveAssignment(
            @PathVariable(name = "courseId") Long courseId,
            @RequestBody @Valid AssignmentCreationDto assignmentCreationDto) {
        log.debug("POST /courses/{}/assignments {}", courseId, assignmentCreationDto);

        try {
            Course course = courseService.getById(courseId);

            Assignment assignment = assignmentAssembler.creationDtoToModel(assignmentCreationDto);
            assignment.setCourse(course);

            course.getAssignments().add(assignment);
            courseService.save(course);

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

        try {
            Course course = courseService.getById(courseId);

            Assignment assignment = course.getAssignments().stream().filter((assignment1 ->
                    assignment1.getId().equals(assignmentId))).findFirst().orElseThrow(() ->
                    new NotFoundException("Assignment with id: " + assignmentId + " doesn't exists!"));

            assignment.setName(assignmentCreationDto.getName());
            assignment.setDueDate(assignmentCreationDto.getDueDate());
            assignment.setPoints(assignmentCreationDto.getPoints());
            assignment.setDescription(assignmentCreationDto.getDescription());

            courseService.save(course);

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

        try {
            Course course = courseService.getById(courseId);

            Assignment assignment = course.getAssignments().stream().filter((assignment1 ->
                    assignment1.getId().equals(assignmentId))).findFirst().orElseThrow(() ->
                    new NotFoundException("Assignment with id: " + assignmentId + " doesn't exists!"));

            course.setAssignments(course.getAssignments().stream().filter((assignment1 ->
                    !assignment1.equals(assignment))).collect(Collectors.toList()));
            courseService.save(course);

            return ResponseEntity.noContent().build();
        } catch (ServiceException se) {
            throw new BadRequestException("Could not DELETE assignment!", se);
        }
    }
}
