package com.bbte.styoudent.api.controller;

import com.bbte.styoudent.api.assembler.SubmissionAssembler;
import com.bbte.styoudent.api.exception.ForbiddenException;
import com.bbte.styoudent.api.exception.InternalServerException;
import com.bbte.styoudent.api.util.AssignmentUtil;
import com.bbte.styoudent.api.util.ParticipationUtil;
import com.bbte.styoudent.dto.outgoing.SubmissionDto;
import com.bbte.styoudent.model.Assignment;
import com.bbte.styoudent.model.Person;
import com.bbte.styoudent.model.Role;
import com.bbte.styoudent.model.Submission;
import com.bbte.styoudent.security.util.AuthUtil;
import com.bbte.styoudent.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/courses/{courseId}/assignments/{assignmentId}/submissions")
public class SubmissionController {
    private final FileStorageService fileStorageService;
    private final PersonService personService;
    private final SubmissionAssembler submissionAssembler;
    private final AssignmentService assignmentService;
    private final SubmissionService submissionService;
    private final AssignmentGradeService assignmentGradeService;
    private final ParticipationUtil participationUtil;
    private final AssignmentUtil assignmentUtil;

    public SubmissionController(FileStorageService fileStorageService, PersonService personService,
                                SubmissionAssembler submissionAssembler, AssignmentService assignmentService,
                                SubmissionService submissionService, AssignmentGradeService assignmentGradeService,
                                ParticipationUtil participationUtil, AssignmentUtil assignmentUtil) {
        this.fileStorageService = fileStorageService;
        this.personService = personService;
        this.submissionAssembler = submissionAssembler;
        this.assignmentService = assignmentService;
        this.submissionService = submissionService;
        this.assignmentGradeService = assignmentGradeService;
        this.participationUtil = participationUtil;
        this.assignmentUtil = assignmentUtil;
    }

    @GetMapping()
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER')")
    public ResponseEntity<List<SubmissionDto>> getSubmissions(
            @PathVariable(name = "courseId") Long courseId,
            @PathVariable(name = "assignmentId") Long assignmentId
    ) {
        log.debug("GET /courses/{}/assignments/{}/submissions", courseId, assignmentId);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());
        participationUtil.checkIfParticipates(courseId, person);
        if (person.getRole().equals(Role.ROLE_STUDENT)) {
            assignmentUtil.checkIfHasThisAssignment(courseId, assignmentId);
        }

        try {
            if (person.getRole().equals(Role.ROLE_STUDENT)) {
                assignmentUtil.checkIfPublished(courseId, assignmentId);

                return ResponseEntity.ok(submissionService.getByAssignmentIdAndBySubmitter(assignmentId, person)
                        .stream().map(submissionAssembler::modelToDto)
                        .collect(Collectors.toList()));
            } else {
                return ResponseEntity.ok(submissionService.getByAssignmentId(assignmentId).stream()
                        .map(submissionAssembler::modelToDto)
                        .collect(Collectors.toList()));
            }
        } catch (ServiceException se) {
            throw new InternalServerException("Could not GET submissions!", se);
        }
    }

    @GetMapping("{submissionId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER')")
    public ResponseEntity<Resource> getSubmission(
            @PathVariable(name = "courseId") Long courseId,
            @PathVariable(name = "assignmentId") Long assignmentId,
            @PathVariable(name = "submissionId") Long submissionId
    ) {
        log.debug("GET /courses/{}/assignments/{}/submissions/{}", courseId, assignmentId, submissionId);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());
        participationUtil.checkIfParticipates(courseId, person);
        assignmentUtil.checkIfHasThisAssignment(courseId, assignmentId);

        try {
            Submission submission = submissionService.getByAssignmentIdAndId(assignmentId, submissionId);

            if (person.getRole().equals(Role.ROLE_STUDENT) && !submission.getSubmitter().equals(person)) {
                throw new ForbiddenException("Access denied!");
            }

            Resource resource = fileStorageService.loadFileAsResource(submission.getUploadedFileName());

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + submission.getFileName() + "\"")
                    .body(resource);
        } catch (ServiceException se) {
            throw new InternalServerException("Could not GET submissions!", se);
        }
    }

    @PostMapping()
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER')")
    public ResponseEntity<SubmissionDto> saveSubmission(
            @PathVariable(name = "courseId") Long courseId,
            @PathVariable(name = "assignmentId") Long assignmentId,
            @RequestParam("file") @NotNull MultipartFile file
    ) {
        log.debug("POST /courses/{}/assignments/{}/submissions", courseId, assignmentId);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());
        participationUtil.checkIfParticipates(courseId, person);
        assignmentUtil.checkIfPublished(courseId, assignmentId);

        try {
            Assignment assignment = assignmentService.getByCourseIdAndId(courseId, assignmentId);

            assignmentGradeService.deleteByAssignmentIdAndSubmitterId(assignment.getId(), person.getId());

            String uploadedFileName = fileStorageService.storeFile(file);

            Submission submission = new Submission();
            submission.setSubmitter(person);
            submission.setAssignment(assignment);
            submission.setDate(LocalDateTime.now());
            submission.setFileName(StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename())));
            submission.setUploadedFileName(uploadedFileName);

            return ResponseEntity.ok(submissionAssembler.modelToDto(
                    submissionService.save(submission)
            ));
        } catch (ServiceException se) {
            throw new InternalServerException("Could not POST submission!", se);
        }
    }
}
