package com.bbte.styoudent.api.controller;

import com.bbte.styoudent.api.assembler.AnnouncementAssembler;
import com.bbte.styoudent.api.assembler.AssignmentAssembler;
import com.bbte.styoudent.api.assembler.CourseAssembler;
import com.bbte.styoudent.api.exception.BadRequestException;
import com.bbte.styoudent.api.exception.ForbiddenException;
import com.bbte.styoudent.api.exception.InternalServerException;
import com.bbte.styoudent.api.exception.NotFoundException;
import com.bbte.styoudent.dto.AnnouncementDto;
import com.bbte.styoudent.dto.AssignmentDto;
import com.bbte.styoudent.dto.CourseDto;
import com.bbte.styoudent.dto.incoming.AnnouncementCreationDto;
import com.bbte.styoudent.dto.incoming.AssignmentCreationDto;
import com.bbte.styoudent.dto.incoming.CourseCreationDto;
import com.bbte.styoudent.dto.incoming.CourseUpdateDto;
import com.bbte.styoudent.dto.outgoing.ApiResponseMessage;
import com.bbte.styoudent.model.Announcement;
import com.bbte.styoudent.model.Assignment;
import com.bbte.styoudent.model.Course;
import com.bbte.styoudent.model.Person;
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
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/courses")
public class CourseController {
    private final CourseService courseService;
    private final CourseAssembler courseAssembler;
    private final PersonService personService;
    private final ParticipationService participationService;
    private final AnnouncementAssembler announcementAssembler;
    private final AssignmentAssembler assignmentAssembler;

    public CourseController(CourseService courseService, CourseAssembler courseAssembler,
                            PersonService personService, ParticipationService participationService,
                            AnnouncementAssembler announcementAssembler, AssignmentAssembler assignmentAssembler) {
        this.courseService = courseService;
        this.courseAssembler = courseAssembler;
        this.personService = personService;
        this.participationService = participationService;
        this.announcementAssembler = announcementAssembler;
        this.assignmentAssembler = assignmentAssembler;
    }

    @GetMapping
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, List<?>>> getCourses() {
        log.debug("GET /courses");

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());

        try {
            return ResponseEntity.ok(Collections.singletonMap("courses",
                    courseService.getAllCoursesByPerson(person)
                            .stream()
                            .map(courseAssembler::modelToDto)
                            .collect(Collectors.toList()))
            );
        } catch (ServiceException se) {
            throw new InternalServerException("Could not GET courses!", se);
        }
    }

    @GetMapping(value = "/{id}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER')")
    public ResponseEntity<CourseDto> getCourse(@PathVariable(name = "id") Long id) {
        log.debug("GET /courses/{}", id);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());

        try {
            Course course = courseService.getCourseByPerson(person, id);
            return ResponseEntity.ok(
                    courseAssembler.modelToDto(course)
            );
        } catch (ServiceException se) {
            throw new NotFoundException("Course with id:  " + id + " doesn't exists!", se);
        }
    }

    @GetMapping(value = "/{courseId}/announcements/{announcementId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER')")
    public ResponseEntity<AnnouncementDto> getAnnouncement(@PathVariable(name = "courseId") Long courseId,
                                                           @PathVariable(name = "announcementId") Long announcementId) {
        log.debug("GET /courses/{}/announcements/{}", courseId, announcementId);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());

        try {
            Course course = courseService.getCourseByPerson(person, courseId);

            Announcement announcement = course.getAnnouncements().stream().filter((announcement1 ->
                    announcement1.getId().equals(announcementId))).findFirst().orElseThrow(() ->
                    new NotFoundException("Announcement with id: " + announcementId + " doesn't exists!"));

            return ResponseEntity.ok(announcementAssembler.modelToDto(announcement));
        } catch (ServiceException se) {
            throw new NotFoundException("Course with id:  " + courseId + " doesn't exists!", se);
        }
    }

    @GetMapping(value = "/{courseId}/assignments/{assignmentId}")
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

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<CourseDto> saveCourse(@RequestBody @Valid CourseCreationDto courseCreationDto) {
        log.debug("POST /courses {}", courseCreationDto);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());

        try {
            Course course = courseAssembler.courseCreationDtoToModel(courseCreationDto, person);
            courseService.save(course);
            participationService.createInitialParticipation(course, person);

            return ResponseEntity.ok(courseAssembler.modelToDto(course));
        } catch (ServiceException se) {
            throw new BadRequestException("Could not POST course!", se);
        }
    }

    @PostMapping("/{courseId}/announcements")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<AnnouncementDto> saveAnnouncement(
            @PathVariable(name = "courseId") Long courseId,
            @RequestBody @Valid AnnouncementCreationDto announcementCreationDto) {
        log.debug("POST /courses/{}/announcements {}", courseId, announcementCreationDto);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());

        try {
            Course course = courseService.getById(courseId);

            Announcement announcement = announcementAssembler.creationDtoToModel(announcementCreationDto);
            announcement.setCourse(course);
            announcement.setCreator(person);
            announcement.setDate(LocalDateTime.now());

            course.getAnnouncements().add(announcement);
            courseService.save(course);

            return ResponseEntity.ok(announcementAssembler.modelToDto(announcement));
        } catch (ServiceException se) {
            throw new BadRequestException("Could not POST announcement!", se);
        }
    }

    @PostMapping("/{courseId}/assignments")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<AssignmentDto> saveAssignment(
            @PathVariable(name = "courseId") Long courseId,
            @RequestBody @Valid AssignmentCreationDto assignmentCreationDto) {
        log.debug("POST /courses/{}/assignments {}", courseId, assignmentCreationDto);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());

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

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<CourseDto> updateCourse(@PathVariable(name = "id") Long id,
                                                  @RequestBody @Valid CourseUpdateDto courseUpdateDto) {
        log.debug("PUT /courses {}", courseUpdateDto);

        try {
            Course course = courseService.getById(id);
            courseAssembler.updateCourseFromDto(courseUpdateDto, course);
            courseService.save(course);

            return ResponseEntity.ok(courseAssembler.modelToDto(course));
        } catch (ServiceException se) {
            throw new BadRequestException("Could not PUT course with id: " + id + "!", se);
        }
    }

    @PutMapping("/{courseId}/announcements/{announcementId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<AnnouncementDto> updateAnnouncement(
            @PathVariable(name = "courseId") Long courseId,
            @PathVariable(name = "announcementId") Long announcementId,
            @RequestBody @Valid AnnouncementCreationDto announcementCreationDto) {
        log.debug("PUT /courses/{}/announcements {}", courseId, announcementCreationDto);

        try {
            Course course = courseService.getById(courseId);

            Announcement announcement = course.getAnnouncements().stream().filter((announcement1 ->
                    announcement1.getId().equals(announcementId))).findFirst().orElseThrow(() ->
                    new NotFoundException("Announcement with id: " + announcementId + " doesn't exists!"));
            announcement.setName(announcementCreationDto.getName());
            announcement.setContent(announcementCreationDto.getContent());

            courseService.save(course);

            return ResponseEntity.ok(announcementAssembler.modelToDto(announcement));
        } catch (ServiceException se) {
            throw new BadRequestException("Could not PUT announcement!", se);
        }
    }

    @PutMapping("/{courseId}/assignments/{assignmentId}")
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

            Assignment newAssignment = assignmentAssembler.creationDtoToModel(assignmentCreationDto);
            assignment.setName(newAssignment.getName());
            assignment.setDueDate(newAssignment.getDueDate());
            assignment.setPoints(newAssignment.getPoints());
            assignment.setDescription(newAssignment.getDescription());

            courseService.save(course);

            return ResponseEntity.ok(assignmentAssembler.modelToDto(assignment));
        } catch (ServiceException se) {
            throw new BadRequestException("Could not PUT assignment!", se);
        }
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponseMessage> deleteCourse(@PathVariable(name = "id") Long id) {
        log.debug("DELETE /courses/{}", id);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());
        Long teacherId;

        try {
            teacherId = courseService.getById(id).getTeacher().getId();
        } catch (ServiceException se) {
            throw new BadRequestException("Course with id: " + id + " doesn't exists!", se);
        }

        if (!teacherId.equals(person.getId())) {
            throw new ForbiddenException("Access denied!");
        }

        try {
            courseService.delete(id);

            return ResponseEntity.noContent().build();
        } catch (ServiceException se) {
            throw new BadRequestException("Could not DELETE course with id " + id + "!", se);
        }
    }

    @DeleteMapping("{courseId}/announcements/{announcementId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> deleteAnnouncement(@PathVariable(name = "courseId") Long courseId,
                                                @PathVariable(name = "announcementId") Long announcementId) {
        log.debug("DELETE /courses/{}/announcements/{}", courseId, announcementId);

        try {
            Course course = courseService.getById(courseId);

            Announcement announcement = course.getAnnouncements().stream().filter((announcement1 ->
                    announcement1.getId().equals(announcementId))).findFirst().orElseThrow(() ->
                    new NotFoundException("Announcement with id: " + announcementId + " doesn't exists!"));

            course.setAnnouncements(course.getAnnouncements().stream().filter((announcement1 ->
                    !announcement1.equals(announcement))).collect(Collectors.toList()));
            courseService.save(course);

            return ResponseEntity.noContent().build();
        } catch (ServiceException se) {
            throw new BadRequestException("Could not POST announcement!", se);
        }
    }

    @DeleteMapping("{courseId}/assignments/{assignmentId}")
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
            throw new BadRequestException("Could not POST assignment!", se);
        }
    }
}
