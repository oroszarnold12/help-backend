package com.bbte.styoudent.api.controller;

import com.bbte.styoudent.api.assembler.AnnouncementCommentAssembler;
import com.bbte.styoudent.api.exception.BadRequestException;
import com.bbte.styoudent.api.exception.ForbiddenException;
import com.bbte.styoudent.api.exception.InternalServerException;
import com.bbte.styoudent.api.exception.NotFoundException;
import com.bbte.styoudent.dto.incoming.AnnouncementCommentCreationDto;
import com.bbte.styoudent.dto.outgoing.AnnouncementCommentDto;
import com.bbte.styoudent.model.Announcement;
import com.bbte.styoudent.model.AnnouncementComment;
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

@Slf4j
@RestController
@RequestMapping("/courses/{courseId}/announcements/{announcementId}/comments")
public class AnnouncementCommentController {
    private final PersonService personService;
    private final CourseService courseService;
    private final AnnouncementCommentAssembler announcementCommentAssembler;
    private final ParticipationService participationService;

    public AnnouncementCommentController(PersonService personService, CourseService courseService,
                                         AnnouncementCommentAssembler announcementCommentAssembler,
                                         ParticipationService participationService) {
        this.personService = personService;
        this.courseService = courseService;
        this.announcementCommentAssembler = announcementCommentAssembler;
        this.participationService = participationService;
    }

    @PostMapping()
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER')")
    public ResponseEntity<AnnouncementCommentDto> saveAnnouncementComment(
            @PathVariable(name = "courseId") Long courseId,
            @PathVariable(name = "announcementId") Long announcementId,
            @RequestBody @Valid AnnouncementCommentCreationDto announcementCommentCreationDto) {
        log.debug("POST /courses/{}/announcements/{}/comments {}", courseId, announcementId,
                announcementCommentCreationDto);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());

        try {
            Course course = getCourse(courseId);
            Announcement announcement = getAnnouncement(course, announcementId);

            checkIfParticipates(course, person);

            AnnouncementComment announcementComment = announcementCommentAssembler.creationDtoToModel(
                    announcementCommentCreationDto
            );
            announcementComment.setDate(LocalDateTime.now());
            announcementComment.setCommenter(person);
            announcementComment.setAnnouncement(announcement);

            announcement.getAnnouncementComments().add(announcementComment);

            courseService.save(course);

            return ResponseEntity.ok(announcementCommentAssembler.modelToDto(announcementComment));
        } catch (ServiceException se) {
            throw new BadRequestException("Could not POST comment!", se);
        }
    }

    @PutMapping("{announcementCommentId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER')")
    public ResponseEntity<AnnouncementCommentDto> updateAnnouncementComment(
            @PathVariable(name = "courseId") Long courseId,
            @PathVariable(name = "announcementId") Long announcementId,
            @PathVariable(name = "announcementCommentId") Long announcementCommentId,
            @RequestBody @Valid AnnouncementCommentCreationDto announcementCommentCreationDto) {
        log.debug("PUT /courses/{}/announcements/{}/comments/{} {}", courseId, announcementId,
                announcementCommentId, announcementCommentCreationDto);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());

        try {
            Course course = getCourse(courseId);
            Announcement announcement = getAnnouncement(course, announcementId);
            AnnouncementComment announcementComment = getComment(announcement, announcementCommentId);

            if (!announcementComment.getCommenter().equals(person)) {
                throw new ForbiddenException("Access denied!");
            }

            announcementComment.setContent(announcementCommentCreationDto.getContent());

            courseService.save(course);

            return ResponseEntity.ok(announcementCommentAssembler.modelToDto(announcementComment));
        } catch (ServiceException se) {
            throw new BadRequestException("Could not PUT comment!", se);
        }
    }

    @DeleteMapping("{announcementCommentId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER')")
    public ResponseEntity<?> deleteAnnouncementComment(
            @PathVariable(name = "courseId") Long courseId,
            @PathVariable(name = "announcementId") Long announcementId,
            @PathVariable(name = "announcementCommentId") Long announcementCommentId) {
        log.debug("DELETE /courses/{}/announcements/{}/comments/{}", courseId, announcementId,
                announcementCommentId);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());

        try {
            Course course = getCourse(courseId);
            Announcement announcement = getAnnouncement(course, announcementId);
            AnnouncementComment announcementComment = getComment(announcement, announcementCommentId);

            if (!announcementComment.getCommenter().equals(person)) {
                throw new ForbiddenException("Access denied!");
            }

            announcement.getAnnouncementComments().remove(announcementComment);

            courseService.save(course);

            return ResponseEntity.noContent().build();
        } catch (ServiceException se) {
            throw new BadRequestException("Could not DELETE comment!", se);
        }
    }

    private AnnouncementComment getComment(Announcement announcement, Long announcementCommentId) {
        return announcement.getAnnouncementComments().stream()
                .filter((announcementComment1 -> announcementComment1.getId().equals(announcementCommentId)))
                .findFirst().orElseThrow(() ->
                        new NotFoundException("Comment with id: " + announcementCommentId + " doesn't exists!"));
    }

    private Course getCourse(Long courseId) {
        return courseService.getById(courseId);
    }

    private Announcement getAnnouncement(Course course, Long announcementId) {
        return course.getAnnouncements().stream().filter((announcement1 ->
                announcement1.getId().equals(announcementId))).findFirst().orElseThrow(() ->
                new NotFoundException("Announcement with id: " + announcementId + " doesn't exists!"));
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
}
