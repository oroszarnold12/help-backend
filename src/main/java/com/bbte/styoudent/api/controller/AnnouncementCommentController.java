package com.bbte.styoudent.api.controller;

import com.bbte.styoudent.api.assembler.AnnouncementCommentAssembler;
import com.bbte.styoudent.api.exception.BadRequestException;
import com.bbte.styoudent.api.exception.ForbiddenException;
import com.bbte.styoudent.api.exception.InternalServerException;
import com.bbte.styoudent.dto.incoming.AnnouncementCommentCreationDto;
import com.bbte.styoudent.dto.outgoing.AnnouncementCommentDto;
import com.bbte.styoudent.model.Announcement;
import com.bbte.styoudent.model.AnnouncementComment;
import com.bbte.styoudent.model.Person;
import com.bbte.styoudent.security.util.AuthUtil;
import com.bbte.styoudent.service.*;
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
    private final AnnouncementCommentAssembler announcementCommentAssembler;
    private final ParticipationService participationService;
    private final AnnouncementService announcementService;
    private final AnnouncementCommentService announcementCommentService;

    public AnnouncementCommentController(PersonService personService,
                                         AnnouncementCommentAssembler announcementCommentAssembler,
                                         ParticipationService participationService,
                                         AnnouncementService announcementService,
                                         AnnouncementCommentService announcementCommentService) {
        this.personService = personService;
        this.announcementCommentAssembler = announcementCommentAssembler;
        this.participationService = participationService;
        this.announcementService = announcementService;
        this.announcementCommentService = announcementCommentService;
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
        checkIfParticipates(courseId, person);

        try {
            Announcement announcement = announcementService.getByCourseIdAndId(courseId, announcementId);

            AnnouncementComment announcementComment = announcementCommentAssembler.creationDtoToModel(
                    announcementCommentCreationDto
            );
            announcementComment.setDate(LocalDateTime.now());
            announcementComment.setCommenter(person);
            announcementComment.setAnnouncement(announcement);

            return ResponseEntity.ok(announcementCommentAssembler.modelToDto(
                    announcementCommentService.save(announcementComment)
            ));
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
        checkIfHasThisAnnouncement(courseId, announcementId);

        try {
            AnnouncementComment announcementComment = announcementCommentService.getByAnnouncementIdAndId(
                    announcementId, announcementCommentId
            );

            if (!announcementComment.getCommenter().equals(person)) {
                throw new ForbiddenException("Access denied!");
            }

            announcementComment.setContent(announcementCommentCreationDto.getContent());

            announcementCommentService.save(announcementComment);

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
        checkIfHasThisAnnouncement(courseId, announcementId);

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

    private void checkIfParticipates(Long courseId, Person person) {
        try {
            if (!participationService.checkIfParticipates(courseId, person)) {
                throw new ForbiddenException("Access denied!");
            }
        } catch (ServiceException se) {
            throw new InternalServerException("Could not check participation!", se);
        }
    }

    private void checkIfHasThisAnnouncement(Long courseId, Long announcementId) {
        try {
            if (!announcementService.checkIfExistsByCourseIdAndId(courseId, announcementId)) {
                throw new BadRequestException(
                        "Course with id: " + courseId + " has no announcement with id: " + announcementId + "!"
                );
            }
        } catch (ServiceException se) {
            throw new InternalServerException("Could not check announcement!", se);
        }
    }
}
