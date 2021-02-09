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
@RequestMapping("/courses/{courseId}/discussions/{discussionId}/comments")
public class DiscussionCommentController {
    private final CourseService courseService;
    private final PersonService personService;
    private final DiscussionCommentAssembler discussionCommentAssembler;
    private final ParticipationService participationService;

    public DiscussionCommentController(CourseService courseService, PersonService personService,
                                       DiscussionCommentAssembler discussionCommentAssembler,
                                       ParticipationService participationService) {
        this.courseService = courseService;
        this.personService = personService;
        this.discussionCommentAssembler = discussionCommentAssembler;
        this.participationService = participationService;
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

        try {
            Course course = getCourse(courseId);
            Discussion discussion = getDiscussion(course, discussionId);

            checkIfParticipates(course, person);

            DiscussionComment discussionComment = discussionCommentAssembler.creationDtoToModel(
                    discussionCommentCreationDto
            );
            discussionComment.setDate(LocalDateTime.now());
            discussionComment.setCommenter(person);
            discussionComment.setDiscussion(discussion);

            discussion.getDiscussionComments().add(discussionComment);

            courseService.save(course);

            return ResponseEntity.ok(discussionCommentAssembler.modelToDto(discussionComment));
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

        try {
            Course course = getCourse(courseId);
            Discussion discussion = getDiscussion(course, discussionId);
            DiscussionComment discussionComment = getComment(discussion, discussionCommentId);

            if (!discussionComment.getCommenter().equals(person)) {
                throw new ForbiddenException("Access denied!");
            }

            discussionComment.setContent(discussionCommentCreationDto.getContent());

            courseService.save(course);

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

        try {
            Course course = getCourse(courseId);
            Discussion discussion = getDiscussion(course, discussionId);
            DiscussionComment discussionComment = getComment(discussion, discussionCommentId);

            if (!discussionComment.getCommenter().equals(person)) {
                throw new ForbiddenException("Access denied!");
            }

            discussion.getDiscussionComments().remove(discussionComment);

            courseService.save(course);

            return ResponseEntity.noContent().build();
        } catch (ServiceException se) {
            throw new BadRequestException("Could not DELETE comment!", se);
        }
    }

    private DiscussionComment getComment(Discussion discussion, Long discussionCommentId) {
        return discussion.getDiscussionComments().stream()
                .filter((discussionComment -> discussionComment.getId().equals(discussionCommentId)))
                .findFirst().orElseThrow(() ->
                        new NotFoundException("Comment with id: " + discussionCommentId + " doesn't exists!"));
    }

    private Course getCourse(Long courseId) {
        return courseService.getById(courseId);
    }

    private Discussion getDiscussion(Course course, Long discussionId) {
        return course.getDiscussions().stream().filter((discussion ->
                discussion.getId().equals(discussionId))).findFirst().orElseThrow(() ->
                new NotFoundException("Discussion with id: " + discussionId + " doesn't exists!"));
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
