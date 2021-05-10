package com.bbte.styoudent.api.controller.assignment;

import com.bbte.styoudent.api.assembler.AssignmentAssembler;
import com.bbte.styoudent.api.exception.BadRequestException;
import com.bbte.styoudent.api.exception.ForbiddenException;
import com.bbte.styoudent.api.exception.InternalServerException;
import com.bbte.styoudent.api.util.AssignmentUtil;
import com.bbte.styoudent.api.util.ParticipationUtil;
import com.bbte.styoudent.dto.outgoing.assignment.AssignmentSubmissionDto;
import com.bbte.styoudent.model.assignment.Assignment;
import com.bbte.styoudent.model.assignment.AssignmentSubmission;
import com.bbte.styoudent.model.assignment.AssignmentSubmissionFile;
import com.bbte.styoudent.model.assignment.AssignmentSubmissionFileObject;
import com.bbte.styoudent.model.person.Person;
import com.bbte.styoudent.model.person.Role;
import com.bbte.styoudent.security.util.AuthUtil;
import com.bbte.styoudent.service.*;
import com.bbte.styoudent.service.assignment.AssignmentService;
import com.bbte.styoudent.service.assignment.AssignmentSubmissionService;
import com.bbte.styoudent.service.person.PersonService;
import com.bbte.styoudent.service.zip.ZipService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Size;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/courses/{courseId}/assignments/{assignmentId}/submissions")
public class AssignmentSubmissionController {
    private final PersonService personService;
    private final AssignmentAssembler assignmentAssembler;
    private final AssignmentService assignmentService;
    private final AssignmentSubmissionService assignmentSubmissionService;
    private final ParticipationUtil participationUtil;
    private final AssignmentUtil assignmentUtil;
    private final ZipService zipService;

    public AssignmentSubmissionController(PersonService personService,
                                          AssignmentAssembler assignmentAssembler, AssignmentService assignmentService,
                                          AssignmentSubmissionService assignmentSubmissionService,
                                          ParticipationUtil participationUtil,
                                          AssignmentUtil assignmentUtil, ZipService zipService) {
        this.personService = personService;
        this.assignmentAssembler = assignmentAssembler;
        this.assignmentService = assignmentService;
        this.assignmentSubmissionService = assignmentSubmissionService;
        this.participationUtil = participationUtil;
        this.assignmentUtil = assignmentUtil;
        this.zipService = zipService;
    }

    @GetMapping()
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER')")
    public ResponseEntity<List<AssignmentSubmissionDto>> getSubmissions(
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

                return ResponseEntity.ok(assignmentSubmissionService
                        .getByAssignmentIdAndBySubmitter(assignmentId, person)
                        .stream().map(assignmentAssembler::modelToDto)
                        .collect(Collectors.toList()));
            } else {
                return ResponseEntity.ok(assignmentSubmissionService.getByAssignmentId(assignmentId).stream()
                        .map(assignmentAssembler::modelToDto)
                        .collect(Collectors.toList()));
            }
        } catch (ServiceException se) {
            throw new InternalServerException("Could not GET submissions!", se);
        }
    }

    @Transactional
    @GetMapping("/files")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Resource> getSubmission(
            @PathVariable(name = "courseId") Long courseId,
            @PathVariable(name = "assignmentId") Long assignmentId
    ) {
        log.debug("GET /courses/{}/assignments/{}/submissions/files", courseId, assignmentId);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());
        participationUtil.checkIfParticipates(courseId, person);
        assignmentUtil.checkIfHasThisAssignment(courseId, assignmentId);

        try {
            List<AssignmentSubmission> assignmentSubmissions = assignmentSubmissionService
                    .getByAssignmentId(assignmentId);

            zipService.open();

            assignmentSubmissions.forEach(assignmentSubmission ->
                    assignmentSubmission.getFiles().forEach(assignmentSubmissionFile -> {
                        String fileName = (assignmentSubmission.getSubmitter().getFirstName()
                                + " " + assignmentSubmission.getSubmitter().getLastName()
                                + "/" + assignmentSubmissionFile.getFileName())
                                .replaceAll("\\s+", "_");

                        zipService.write(fileName, assignmentSubmissionFile.getFileObject().getBytes());
                    }));

            zipService.close();

            return ResponseEntity.ok()
                    .body(
                            new ByteArrayResource(zipService.getBytes())
                    );
        } catch (ServiceException se) {
            throw new InternalServerException("Could not GET files of submissions!", se);
        }
    }

    @Transactional
    @GetMapping("{submissionId}/files/{fileId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER')")
    public ResponseEntity<Resource> getSubmission(
            @PathVariable(name = "courseId") Long courseId,
            @PathVariable(name = "assignmentId") Long assignmentId,
            @PathVariable(name = "submissionId") Long submissionId,
            @PathVariable(name = "fileId") Long fileId
    ) {
        log.debug("GET /courses/{}/assignments/{}/submissions/{}/files/{}", courseId, assignmentId, submissionId,
                fileId);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());
        participationUtil.checkIfParticipates(courseId, person);
        assignmentUtil.checkIfHasThisAssignment(courseId, assignmentId);

        try {
            AssignmentSubmission assignmentSubmission = assignmentSubmissionService
                    .getByAssignmentIdAndId(assignmentId, submissionId);

            if (person.getRole().equals(Role.ROLE_STUDENT) && !assignmentSubmission.getSubmitter().equals(person)) {
                throw new ForbiddenException("Access denied!");
            }

            AssignmentSubmissionFile assignmentSubmissionFile = assignmentSubmission.getFiles()
                    .stream().filter((file) ->
                            file.getId().equals(fileId)).findFirst().orElseThrow(() ->
                            new BadRequestException("Assignment submission has no file with id:" + fileId + "!"));

            return ResponseEntity.ok()
                    .body(
                            new ByteArrayResource(assignmentSubmissionFile.getFileObject().getBytes())
                    );
        } catch (ServiceException se) {
            throw new InternalServerException("Could not GET submission file!", se);
        }
    }

    @Transactional
    @PostMapping()
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER')")
    public ResponseEntity<AssignmentSubmissionDto> saveSubmission(
            @PathVariable(name = "courseId") Long courseId,
            @PathVariable(name = "assignmentId") Long assignmentId,
            @RequestParam("files") @Size(min = 1, max = 5) MultipartFile... files
    ) {
        log.debug("POST /courses/{}/assignments/{}/submissions", courseId, assignmentId);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());
        participationUtil.checkIfParticipates(courseId, person);
        assignmentUtil.checkIfPublished(courseId, assignmentId);

        try {
            Assignment assignment = assignmentService.getByCourseIdAndId(courseId, assignmentId);

            AssignmentSubmission assignmentSubmission = new AssignmentSubmission();
            assignmentSubmission.setSubmitter(person);
            assignmentSubmission.setAssignment(assignment);
            assignmentSubmission.setDate(LocalDateTime.now());

            List<AssignmentSubmissionFile> fileObjects = Arrays.stream(files).map((file) -> {
                AssignmentSubmissionFile assignmentSubmissionFile = new AssignmentSubmissionFile();
                try {
                    AssignmentSubmissionFileObject fileObject = new AssignmentSubmissionFileObject();
                    fileObject.setBytes(file.getBytes());
                    assignmentSubmissionFile.setFileObject(fileObject);
                } catch (IOException e) {
                    throw new BadRequestException(
                            "Could not process file with name: " + file.getOriginalFilename() + "!"
                    );
                }
                assignmentSubmissionFile.setAssignmentSubmission(assignmentSubmission);
                assignmentSubmissionFile.setFileName(
                        assignmentUtil.getCorrectedFileName(
                                assignment.getAssignmentSubmissions(),
                                StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()))
                        )
                );

                return assignmentSubmissionFile;
            }).collect(Collectors.toList());

            assignmentSubmission.setFiles(fileObjects);
            AssignmentSubmission result = assignmentSubmissionService.save(assignmentSubmission);

            if (assignmentUtil.checkIfGraded(assignmentId, person)) {
                assignmentUtil.createMultipleNotificationsOfAssignmentSubmissionCreation(assignment);
            }

            return ResponseEntity.ok(assignmentAssembler.modelToDto(result));
        } catch (ServiceException se) {
            throw new InternalServerException("Could not POST submission!", se);
        }
    }
}
