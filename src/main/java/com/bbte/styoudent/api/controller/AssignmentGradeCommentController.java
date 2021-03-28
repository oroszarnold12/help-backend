package com.bbte.styoudent.api.controller;

import com.bbte.styoudent.api.assembler.AssignmentGradeCommentAssembler;
import com.bbte.styoudent.api.exception.BadRequestException;
import com.bbte.styoudent.api.exception.ForbiddenException;
import com.bbte.styoudent.api.util.AssignmentUtil;
import com.bbte.styoudent.api.util.ParticipationUtil;
import com.bbte.styoudent.dto.incoming.AssignmentGradeCommentCreationDto;
import com.bbte.styoudent.dto.outgoing.AssignmentGradeCommentDto;
import com.bbte.styoudent.model.AssignmentGrade;
import com.bbte.styoudent.model.AssignmentGradeComment;
import com.bbte.styoudent.model.Person;
import com.bbte.styoudent.security.util.AuthUtil;
import com.bbte.styoudent.service.AssignmentGradeCommentService;
import com.bbte.styoudent.service.AssignmentGradeService;
import com.bbte.styoudent.service.PersonService;
import com.bbte.styoudent.service.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;

@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/courses/{courseId}/assignments/{assignmentId}/grades/{gradeId}/comments")
public class AssignmentGradeCommentController {
    private final PersonService personService;
    private final ParticipationUtil participationUtil;
    private final AssignmentGradeService assignmentGradeService;
    private final AssignmentGradeCommentAssembler assignmentGradeCommentAssembler;
    private final AssignmentGradeCommentService assignmentGradeCommentService;
    private final AssignmentUtil assignmentUtil;

    public AssignmentGradeCommentController(PersonService personService, ParticipationUtil participationUtil,
                                            AssignmentGradeService assignmentGradeService,
                                            AssignmentGradeCommentAssembler assignmentGradeCommentAssembler,
                                            AssignmentGradeCommentService assignmentGradeCommentService,
                                            AssignmentUtil assignmentUtil) {
        this.personService = personService;
        this.participationUtil = participationUtil;
        this.assignmentGradeService = assignmentGradeService;
        this.assignmentGradeCommentAssembler = assignmentGradeCommentAssembler;
        this.assignmentGradeCommentService = assignmentGradeCommentService;
        this.assignmentUtil = assignmentUtil;
    }

    @PostMapping()
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER')")
    public ResponseEntity<AssignmentGradeCommentDto> saveAssignmentGradeComment(
            @PathVariable(name = "courseId") Long courseId,
            @PathVariable(name = "assignmentId") Long assignmentId,
            @PathVariable(name = "gradeId") Long gradeId,
            @RequestBody @Valid AssignmentGradeCommentCreationDto assignmentGradeCommentCreationDto) {
        log.debug("POST /courses/{}/assignments/{}/grades/{}/comments {}", courseId, assignmentId, gradeId,
                assignmentGradeCommentCreationDto);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());
        participationUtil.checkIfParticipates(courseId, person);
        assignmentUtil.checkIfHasThisAssignment(courseId, assignmentId);

        try {
            AssignmentGrade assignmentGrade = assignmentGradeService.getByAssignmentIdAndId(assignmentId, gradeId);

            AssignmentGradeComment assignmentGradeComment = assignmentGradeCommentAssembler.creationDtoToModel(
                    assignmentGradeCommentCreationDto
            );
            assignmentGradeComment.setDate(LocalDateTime.now());
            assignmentGradeComment.setCommenter(person);
            assignmentGradeComment.setAssignmentGrade(assignmentGrade);

            return ResponseEntity.ok(assignmentGradeCommentAssembler.modelToDto(
                    assignmentGradeCommentService.save(assignmentGradeComment)
            ));
        } catch (ServiceException se) {
            throw new BadRequestException("Could not POST comment!", se);
        }
    }

    @PutMapping("{commentId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER')")
    public ResponseEntity<AssignmentGradeCommentDto> updateAssignmentGradeComment(
            @PathVariable(name = "courseId") Long courseId,
            @PathVariable(name = "assignmentId") Long assignmentId,
            @PathVariable(name = "gradeId") Long gradeId,
            @PathVariable(name = "commentId") Long commentId,
            @RequestBody @Valid AssignmentGradeCommentCreationDto assignmentGradeCommentCreationDto) {
        log.debug("PUT /courses/{}/assignments/{}/grades/{}/comments/{} {}", courseId, assignmentId,
                gradeId, commentId, assignmentGradeCommentCreationDto);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());
        assignmentUtil.checkIfHasThisAssignment(courseId, assignmentId);
        assignmentUtil.checkIfHasThisGrade(assignmentId, gradeId);

        try {
            AssignmentGradeComment assignmentGradeComment = assignmentGradeCommentService.getByAssignmentGradeIdAndId(
                    gradeId, commentId
            );

            if (!assignmentGradeComment.getCommenter().equals(person)) {
                throw new ForbiddenException("Access denied!");
            }

            assignmentGradeComment.setContent(assignmentGradeCommentCreationDto.getContent());

            assignmentGradeCommentService.save(assignmentGradeComment);

            return ResponseEntity.ok(assignmentGradeCommentAssembler.modelToDto(assignmentGradeComment));
        } catch (ServiceException se) {
            throw new BadRequestException("Could not PUT comment!", se);
        }
    }

    @DeleteMapping("{commentId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER')")
    public ResponseEntity<?> deleteAssignmentGradeComment(
            @PathVariable(name = "courseId") Long courseId,
            @PathVariable(name = "assignmentId") Long assignmentId,
            @PathVariable(name = "gradeId") Long gradeId,
            @PathVariable(name = "commentId") Long commentId) {
        log.debug("DELETE /courses/{}/assignments/{}/grades/{}/comments/{}", courseId, assignmentId,
                gradeId, commentId);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());
        assignmentUtil.checkIfHasThisAssignment(courseId, assignmentId);
        assignmentUtil.checkIfHasThisGrade(assignmentId, gradeId);

        try {
            AssignmentGradeComment assignmentGradeComment = assignmentGradeCommentService.getByAssignmentGradeIdAndId(
                    gradeId, commentId
            );

            if (!assignmentGradeComment.getCommenter().equals(person)) {
                throw new ForbiddenException("Access denied!");
            }

            assignmentGradeCommentService.deleteById(commentId);

            return ResponseEntity.noContent().build();
        } catch (ServiceException se) {
            throw new BadRequestException("Could not DELETE comment!", se);
        }
    }
}
