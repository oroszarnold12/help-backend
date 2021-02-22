package com.bbte.styoudent.api.controller;

import com.bbte.styoudent.api.assembler.DiscussionCommentAssembler;
import com.bbte.styoudent.api.exception.BadRequestException;
import com.bbte.styoudent.api.exception.ForbiddenException;
import com.bbte.styoudent.api.exception.InternalServerException;
import com.bbte.styoudent.api.exception.NotFoundException;
import com.bbte.styoudent.dto.incoming.DiscussionCommentCreationDto;
import com.bbte.styoudent.dto.outgoing.DiscussionCommentDto;
import com.bbte.styoudent.model.*;
import com.bbte.styoudent.security.util.AuthUtil;
import com.bbte.styoudent.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/courses/{courseId}/discussions/{discussionId}/comments")
public class DiscussionCommentController {
    private final PersonService personService;
    private final DiscussionCommentAssembler discussionCommentAssembler;
    private final ParticipationService participationService;
    private final DiscussionService discussionService;
    private final DiscussionCommentService discussionCommentService;

    public DiscussionCommentController(PersonService personService,
                                       DiscussionCommentAssembler discussionCommentAssembler,
                                       ParticipationService participationService, DiscussionService discussionService,
                                       DiscussionCommentService discussionCommentService) {
        this.personService = personService;
        this.discussionCommentAssembler = discussionCommentAssembler;
        this.participationService = participationService;
        this.discussionService = discussionService;
        this.discussionCommentService = discussionCommentService;
    }

    @PostMapping()
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER')")
    public ResponseEntity<DiscussionCommentDto> saveDiscussionComment(
            @PathVariable(name = "courseId") Long courseId,
            @PathVariable(name = "discussionId") Long discussionId,
            @RequestBody @Valid DiscussionCommentCreationDto discussionCommentCreationDto) {
        log.debug("POST /courses/{}/discussions/{}/comments {}", courseId, discussionId,
                discussionCommentCreationDto);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());
        checkIfParticipates(courseId, person);

        try {
            Discussion discussion = discussionService.getByCourseIdAndId(courseId, discussionId);

            DiscussionComment discussionComment = discussionCommentAssembler.creationDtoToModel(
                    discussionCommentCreationDto
            );
            discussionComment.setDate(LocalDateTime.now());
            discussionComment.setCommenter(person);
            discussionComment.setDiscussion(discussion);

            return ResponseEntity.ok(discussionCommentAssembler.modelToDto(
                    discussionCommentService.save(discussionComment)
            ));
        } catch (ServiceException se) {
            throw new BadRequestException("Could not POST comment!", se);
        }
    }

    @PutMapping("{discussionCommentId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER')")
    public ResponseEntity<DiscussionCommentDto> updateDiscussionComment(
            @PathVariable(name = "courseId") Long courseId,
            @PathVariable(name = "discussionId") Long discussionId,
            @PathVariable(name = "discussionCommentId") Long discussionCommentId,
            @RequestBody @Valid DiscussionCommentCreationDto discussionCommentCreationDto) {
        log.debug("PUT /courses/{}/discussions/{}/comments/{} {}", courseId, discussionId,
                discussionCommentId, discussionCommentCreationDto);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());
        checkIfHasThisDiscussion(courseId, discussionId);

        try {
            DiscussionComment discussionComment = discussionCommentService.getByDiscussionIdAndId(discussionId,
                    discussionCommentId);

            if (!discussionComment.getCommenter().equals(person)) {
                throw new ForbiddenException("Access denied!");
            }

            discussionComment.setContent(discussionCommentCreationDto.getContent());

            discussionCommentService.save(discussionComment);

            return ResponseEntity.ok(discussionCommentAssembler.modelToDto(discussionComment));
        } catch (ServiceException se) {
            throw new BadRequestException("Could not PUT comment!", se);
        }
    }

    @DeleteMapping("{discussionCommentId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER')")
    public ResponseEntity<?> deleteDiscussionComment(
            @PathVariable(name = "courseId") Long courseId,
            @PathVariable(name = "discussionId") Long discussionId,
            @PathVariable(name = "discussionCommentId") Long discussionCommentId) {
        log.debug("DELETE /courses/{}/discussions/{}/comments/{}", courseId, discussionId,
                discussionCommentId);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());
        checkIfHasThisDiscussion(courseId, discussionId);

        try {
            DiscussionComment discussionComment = discussionCommentService.getByDiscussionIdAndId(discussionId,
                    discussionCommentId);

            if (!discussionComment.getCommenter().equals(person)) {
                throw new ForbiddenException("Access denied!");
            }

            discussionCommentService.delete(discussionCommentId);

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

    private void checkIfHasThisDiscussion(Long courseId, Long discussionId) {
        try {
            if (!discussionService.checkIfExistsByCourseIdAndId(courseId, discussionId)) {
                throw new BadRequestException(
                        "Course with id: " + courseId + " has no discussion with id: " + discussionId + "!"
                );
            }
        } catch (ServiceException se) {
            throw new InternalServerException("Could not check discussion!", se);
        }
    }
}
