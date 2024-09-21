package com.help.api.controller.announcement;

import com.help.api.assembler.AnnouncementAssembler;
import com.help.api.exception.BadRequestException;
import com.help.api.exception.ForbiddenException;
import com.help.api.util.AnnouncementUtil;
import com.help.api.util.ParticipationUtil;
import com.help.dto.incoming.announcement.AnnouncementCommentCreationDto;
import com.help.dto.outgoing.announcement.AnnouncementCommentDto;
import com.help.model.announcement.Announcement;
import com.help.model.announcement.AnnouncementComment;
import com.help.model.person.Person;
import com.help.security.util.AuthUtil;
import com.help.service.ServiceException;
import com.help.service.announcement.AnnouncementCommentService;
import com.help.service.announcement.AnnouncementService;
import com.help.service.person.PersonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;

@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/courses/{courseId}/announcements/{announcementId}/comments")
public class AnnouncementCommentController {
    private final PersonService personService;
    private final AnnouncementAssembler announcementAssembler;
    private final AnnouncementService announcementService;
    private final AnnouncementCommentService announcementCommentService;
    private final ParticipationUtil participationUtil;
    private final AnnouncementUtil announcementUtil;

    public AnnouncementCommentController(PersonService personService,
                                         AnnouncementAssembler announcementAssembler,
                                         AnnouncementService announcementService,
                                         AnnouncementCommentService announcementCommentService,
                                         ParticipationUtil participationUtil, AnnouncementUtil announcementUtil) {
        this.personService = personService;
        this.announcementAssembler = announcementAssembler;
        this.announcementService = announcementService;
        this.announcementCommentService = announcementCommentService;
        this.participationUtil = participationUtil;
        this.announcementUtil = announcementUtil;
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
        participationUtil.checkIfParticipates(courseId, person);

        try {
            Announcement announcement = announcementService.getByCourseIdAndId(courseId, announcementId);

            AnnouncementComment announcementComment = announcementAssembler.creationDtoToModel(
                    announcementCommentCreationDto
            );
            announcementComment.setDate(LocalDateTime.now());
            announcementComment.setCommenter(person);
            announcementComment.setAnnouncement(announcement);

            announcementComment = announcementCommentService.save(announcementComment);

            announcementUtil.createMultipleNotificationsOfAnnouncementComment(announcementComment);

            return ResponseEntity.ok(announcementAssembler.modelToDto(announcementComment));
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
        announcementUtil.checkIfHasThisAnnouncement(courseId, announcementId);

        try {
            AnnouncementComment announcementComment = announcementCommentService.getByAnnouncementIdAndId(
                    announcementId, announcementCommentId
            );

            if (!announcementComment.getCommenter().equals(person)) {
                throw new ForbiddenException("Access denied!");
            }

            announcementComment.setContent(announcementCommentCreationDto.getContent());

            announcementCommentService.save(announcementComment);

            return ResponseEntity.ok(announcementAssembler.modelToDto(announcementComment));
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
        announcementUtil.checkIfHasThisAnnouncement(courseId, announcementId);

        try {
            AnnouncementComment announcementComment = announcementCommentService.getByAnnouncementIdAndId(
                    announcementId, announcementCommentId
            );

            if (!announcementComment.getCommenter().equals(person)) {
                throw new ForbiddenException("Access denied!");
            }

            announcementCommentService.deleteById(announcementCommentId);

            return ResponseEntity.noContent().build();
        } catch (ServiceException se) {
            throw new BadRequestException("Could not DELETE comment!", se);
        }
    }
}
