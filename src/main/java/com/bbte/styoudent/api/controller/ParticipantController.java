package com.bbte.styoudent.api.controller;

import com.bbte.styoudent.api.assembler.PersonAssembler;
import com.bbte.styoudent.api.exception.BadRequestException;
import com.bbte.styoudent.api.exception.InternalServerException;
import com.bbte.styoudent.api.util.ParticipantUtil;
import com.bbte.styoudent.api.util.ParticipationUtil;
import com.bbte.styoudent.dto.outgoing.ThinPersonDto;
import com.bbte.styoudent.model.Course;
import com.bbte.styoudent.model.Person;
import com.bbte.styoudent.security.util.AuthUtil;
import com.bbte.styoudent.service.CourseService;
import com.bbte.styoudent.service.ParticipationService;
import com.bbte.styoudent.service.PersonService;
import com.bbte.styoudent.service.ServiceException;
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
    ResponseEntity<List<ThinPersonDto>> getParticipants(@PathVariable("courseId") Long courseId) {
        log.debug("GET /courses/{}/participants", courseId);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());
        participationUtil.checkIfParticipates(courseId, person);

        try {
            Course course = courseService.getById(courseId);

            return ResponseEntity.ok(
                    personService.getByCoursesContains(course)
                            .stream().map(personAssembler::modelToThinDto)
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
            throw new BadRequestException("Course with id: " + courseId +
                    " has no participant with id: " + participantId + "!", serviceException);
        }
    }
}
