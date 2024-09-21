package com.help.api.controller.discussion;

import com.help.api.assembler.DiscussionAssembler;
import com.help.api.exception.BadRequestException;
import com.help.api.exception.ForbiddenException;
import com.help.api.util.DiscussionUtil;
import com.help.api.util.ParticipationUtil;
import com.help.dto.incoming.discussion.DiscussionCommentCreationDto;
import com.help.dto.outgoing.discussion.DiscussionCommentDto;
import com.help.model.discussion.Discussion;
import com.help.model.discussion.DiscussionComment;
import com.help.model.person.Person;
import com.help.security.util.AuthUtil;
import com.help.service.ServiceException;
import com.help.service.discussion.DiscussionCommentService;
import com.help.service.discussion.DiscussionService;
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
@RequestMapping("/courses/{courseId}/discussions/{discussionId}/comments")
public class DiscussionCommentController {
    private final PersonService personService;
    private final DiscussionAssembler discussionAssembler;
    private final DiscussionService discussionService;
    private final DiscussionCommentService discussionCommentService;
    private final DiscussionUtil discussionUtil;
    private final ParticipationUtil participationUtil;

    public DiscussionCommentController(PersonService personService,
                                       DiscussionAssembler discussionAssembler,
                                       DiscussionService discussionService,
                                       DiscussionCommentService discussionCommentService, DiscussionUtil discussionUtil,
                                       ParticipationUtil participationUtil) {
        this.personService = personService;
        this.discussionAssembler = discussionAssembler;
        this.discussionService = discussionService;
        this.discussionCommentService = discussionCommentService;
        this.discussionUtil = discussionUtil;
        this.participationUtil = participationUtil;
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
        participationUtil.checkIfParticipates(courseId, person);

        try {
            Discussion discussion = discussionService.getByCourseIdAndId(courseId, discussionId);

            DiscussionComment discussionComment = discussionAssembler.creationDtoToModel(
                    discussionCommentCreationDto
            );
            discussionComment.setDate(LocalDateTime.now());
            discussionComment.setCommenter(person);
            discussionComment.setDiscussion(discussion);

            discussionComment = discussionCommentService.save(discussionComment);

            discussionUtil.createMultipleNotificationsOfDiscussionComment(discussionComment);

            return ResponseEntity.ok(discussionAssembler.modelToDto(discussionComment));
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
        discussionUtil.checkIfHasThisDiscussion(courseId, discussionId);

        try {
            DiscussionComment discussionComment = discussionCommentService.getByDiscussionIdAndId(discussionId,
                    discussionCommentId);

            if (!discussionComment.getCommenter().equals(person)) {
                throw new ForbiddenException("Access denied!");
            }

            discussionComment.setContent(discussionCommentCreationDto.getContent());

            discussionCommentService.save(discussionComment);

            return ResponseEntity.ok(discussionAssembler.modelToDto(discussionComment));
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
        discussionUtil.checkIfHasThisDiscussion(courseId, discussionId);

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
}
