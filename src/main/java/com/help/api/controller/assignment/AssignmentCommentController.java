package com.help.api.controller.assignment;

import com.help.api.assembler.AssignmentAssembler;
import com.help.api.exception.BadRequestException;
import com.help.api.exception.ForbiddenException;
import com.help.api.util.AssignmentUtil;
import com.help.api.util.ParticipationUtil;
import com.help.dto.incoming.assignment.AssignmentCommentCreationDto;
import com.help.dto.incoming.assignment.AssignmentCommentUpdateDto;
import com.help.dto.outgoing.assignment.AssignmentCommentDto;
import com.help.model.assignment.Assignment;
import com.help.model.assignment.AssignmentComment;
import com.help.model.person.Person;
import com.help.model.person.Role;
import com.help.security.util.AuthUtil;
import com.help.service.assignment.AssignmentCommentService;
import com.help.service.assignment.AssignmentService;
import com.help.service.person.PersonService;
import com.help.service.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;

@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/courses/{courseId}/assignments/{assignmentId}/comments")
public class AssignmentCommentController {
    private final PersonService personService;
    private final ParticipationUtil participationUtil;
    private final AssignmentAssembler assignmentAssembler;
    private final AssignmentCommentService assignmentCommentService;
    private final AssignmentUtil assignmentUtil;
    private final AssignmentService assignmentService;

    public AssignmentCommentController(PersonService personService, ParticipationUtil participationUtil,
                                       AssignmentAssembler assignmentAssembler,
                                       AssignmentCommentService assignmentCommentService,
                                       AssignmentUtil assignmentUtil, AssignmentService assignmentService) {
        this.personService = personService;
        this.participationUtil = participationUtil;
        this.assignmentAssembler = assignmentAssembler;
        this.assignmentCommentService = assignmentCommentService;
        this.assignmentUtil = assignmentUtil;
        this.assignmentService = assignmentService;
    }

    @PostMapping()
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER')")
    public ResponseEntity<AssignmentCommentDto> saveAssignmentGradeComment(
            @PathVariable(name = "courseId") Long courseId,
            @PathVariable(name = "assignmentId") Long assignmentId,
            @RequestBody @Valid AssignmentCommentCreationDto assignmentCommentCreationDto) {
        log.debug("POST /courses/{}/assignments/{}/comments {}", courseId, assignmentId,
                assignmentCommentCreationDto);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());
        participationUtil.checkIfParticipates(courseId, person);

        try {
            Person recipient;
            if (person.getRole().equals(Role.ROLE_TEACHER)) {
                recipient = personService.getPersonByEmail(assignmentCommentCreationDto.getRecipientEmail());
                participationUtil.checkIfParticipates(courseId, recipient);
            } else {
                recipient = person;
            }

            Assignment assignment = assignmentService.getByCourseIdAndId(courseId, assignmentId);

            AssignmentComment assignmentComment = new AssignmentComment();
            assignmentComment.setContent(assignmentCommentCreationDto.getContent());
            assignmentComment.setDate(LocalDateTime.now());
            assignmentComment.setCommenter(person);
            assignmentComment.setRecipient(recipient);
            assignmentComment.setAssignment(assignment);

            assignmentComment = assignmentCommentService.save(assignmentComment);

            if (person.getRole().equals(Role.ROLE_TEACHER)) {
                assignmentUtil.createSingleNotificationOfAssignmentSubmissionComment(assignmentComment, recipient);
            } else {
                assignmentUtil.createMultipleNotificationsOfAssignmentSubmissionComment(assignmentComment);
            }

            return ResponseEntity.ok(assignmentAssembler.modelToDto(assignmentComment));
        } catch (ServiceException se) {
            throw new BadRequestException("Could not POST comment!", se);
        }
    }

    @PutMapping("{commentId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER')")
    public ResponseEntity<AssignmentCommentDto> updateAssignmentGradeComment(
            @PathVariable(name = "courseId") Long courseId,
            @PathVariable(name = "assignmentId") Long assignmentId,
            @PathVariable(name = "commentId") Long commentId,
            @RequestBody @Valid AssignmentCommentUpdateDto assignmentCommentUpdateDto) {
        log.debug("PUT /courses/{}/assignments/{}/comments/{} {}", courseId, assignmentId,
                commentId, assignmentCommentUpdateDto);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());
        assignmentUtil.checkIfHasThisAssignment(courseId, assignmentId);

        try {
            AssignmentComment assignmentComment = assignmentCommentService.getByAssignmentIdAndId(
                    assignmentId, commentId
            );

            if (!assignmentComment.getCommenter().equals(person)) {
                throw new ForbiddenException("Access denied!");
            }

            assignmentComment.setContent(assignmentCommentUpdateDto.getContent());

            assignmentCommentService.save(assignmentComment);

            return ResponseEntity.ok(assignmentAssembler.modelToDto(assignmentComment));
        } catch (ServiceException se) {
            throw new BadRequestException("Could not PUT comment!", se);
        }
    }

    @DeleteMapping("{commentId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER')")
    public ResponseEntity<?> deleteAssignmentGradeComment(
            @PathVariable(name = "courseId") Long courseId,
            @PathVariable(name = "assignmentId") Long assignmentId,
            @PathVariable(name = "commentId") Long commentId) {
        log.debug("DELETE /courses/{}/assignments/{}/comments/{}", courseId, assignmentId,
                commentId);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());
        assignmentUtil.checkIfHasThisAssignment(courseId, assignmentId);

        try {
            AssignmentComment assignmentComment = assignmentCommentService.getByAssignmentIdAndId(
                    assignmentId, commentId
            );

            if (!assignmentComment.getCommenter().equals(person)) {
                throw new ForbiddenException("Access denied!");
            }

            assignmentCommentService.deleteById(commentId);

            return ResponseEntity.noContent().build();
        } catch (ServiceException se) {
            throw new BadRequestException("Could not DELETE comment!", se);
        }
    }
}
