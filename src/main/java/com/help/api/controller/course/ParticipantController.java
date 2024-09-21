package com.help.api.controller.course;

import com.help.api.assembler.PersonAssembler;
import com.help.api.exception.BadRequestException;
import com.help.api.exception.InternalServerException;
import com.help.api.util.ParticipantUtil;
import com.help.api.util.ParticipationUtil;
import com.help.dto.outgoing.person.PersonDto;
import com.help.model.course.Course;
import com.help.model.person.Person;
import com.help.security.util.AuthUtil;
import com.help.service.course.CourseService;
import com.help.service.person.ParticipationService;
import com.help.service.person.PersonService;
import com.help.service.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/courses/{courseId}/participants")
public class ParticipantController {
    private final PersonService personService;
    private final ParticipationUtil participationUtil;
    private final CourseService courseService;
    private final PersonAssembler personAssembler;
    private final ParticipationService participationService;
    private final ParticipantUtil participantUtil;

    public ParticipantController(PersonService personService, ParticipationUtil participationUtil,
                                 CourseService courseService, PersonAssembler personAssembler,
                                 ParticipationService participationService, ParticipantUtil participantUtil) {
        this.personService = personService;
        this.participationUtil = participationUtil;
        this.courseService = courseService;
        this.personAssembler = personAssembler;
        this.participationService = participationService;
        this.participantUtil = participantUtil;
    }

    @GetMapping
    @PreAuthorize("hasRole('TEACHER') or hasRole('STUDENT')")
    ResponseEntity<List<PersonDto>> getParticipants(@PathVariable("courseId") Long courseId) {
        log.debug("GET /courses/{}/participants", courseId);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());
        participationUtil.checkIfParticipates(courseId, person);

        try {
            Course course = courseService.getById(courseId);

            return ResponseEntity.ok(
                    personService.getByCoursesContains(course)
                            .stream().map(personAssembler::modelToDto)
                            .collect(Collectors.toList()));

        } catch (ServiceException se) {
            throw new InternalServerException("Could not GET participants!", se);
        }
    }

    @DeleteMapping("{participantId}")
    @PreAuthorize("hasRole('TEACHER')")
    ResponseEntity<?> kickParticipant(@PathVariable("courseId") Long courseId,
                                      @PathVariable("participantId") Long participantId) {
        log.debug("DELETE /courses/{}/participants/{}", courseId, participantId);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());
        participationUtil.checkIfParticipates(courseId, person);

        try {
            if (person.getId().equals(participantId)) {
                throw new AccessDeniedException("Access denied!");
            }

            participationService.deleteByParticipantIdAndCourseId(participantId, courseId);

            participantUtil.createSingleNotificationOfKick(participantId, courseId);

            return ResponseEntity.noContent().build();
        } catch (ServiceException serviceException) {
            throw new BadRequestException("Course with id: " + courseId
                    + " has no participant with id: " + participantId + "!", serviceException);
        }
    }
}
