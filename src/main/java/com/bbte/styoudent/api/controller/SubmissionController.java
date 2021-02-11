package com.bbte.styoudent.api.controller;

import com.bbte.styoudent.api.assembler.SubmissionAssembler;
import com.bbte.styoudent.api.exception.ForbiddenException;
import com.bbte.styoudent.api.exception.InternalServerException;
import com.bbte.styoudent.api.exception.NotFoundException;
import com.bbte.styoudent.dto.incoming.SubmissionGradeDto;
import com.bbte.styoudent.dto.outgoing.SubmissionDto;
import com.bbte.styoudent.model.*;
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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/courses/{courseId}/assignments/{assignmentId}/submissions")
public class SubmissionController {
    private final FileStorageService fileStorageService;
    private final PersonService personService;
    private final CourseService courseService;
    private final ParticipationService participationService;
    private final SubmissionAssembler submissionAssembler;

    public SubmissionController(FileStorageService fileStorageService, PersonService personService,
                                CourseService courseService, ParticipationService participationService,
                                SubmissionAssembler submissionAssembler) {
        this.fileStorageService = fileStorageService;
        this.personService = personService;
        this.courseService = courseService;
        this.participationService = participationService;
        this.submissionAssembler = submissionAssembler;
    }

    @GetMapping()
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER')")
    public ResponseEntity<List<SubmissionDto>> getSubmissions(
            @PathVariable(name = "courseId") Long courseId,
            @PathVariable(name = "assignmentId") Long assignmentId
    ) {
        log.debug("GET /courses/{}/assignments/{}/submissions", courseId, assignmentId);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());

        try {
            Course course = courseService.getById(courseId);

            checkIfParticipates(course, person);

            Assignment assignment = getAssignment(course, assignmentId);

            if (person.getRole().equals(Role.ROLE_STUDENT)) {
                return ResponseEntity.ok(assignment.getSubmissions().stream().filter((submission ->
                        submission.getSubmitter().equals(person)))
                        .map(submissionAssembler::modelToDto)
                        .collect(Collectors.toList()));
            } else {
                return ResponseEntity.ok(assignment.getSubmissions().stream()
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

        try {
            Course course = courseService.getById(courseId);

            checkIfParticipates(course, person);

            Assignment assignment = getAssignment(course, assignmentId);

            Submission submission = getSubmission(assignment, submissionId);

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

        try {
            Course course = courseService.getById(courseId);

            checkIfParticipates(course, person);

            Assignment assignment = getAssignment(course, assignmentId);

            String uploadedFileName = fileStorageService.storeFile(file);

            Submission submission = new Submission();
            submission.setSubmitter(person);
            submission.setAssignment(assignment);
            submission.setDate(LocalDateTime.now());
            submission.setFileName(StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename())));
            submission.setUploadedFileName(uploadedFileName);

            assignment.getSubmissions().add(submission);

            this.courseService.save(course);

            return ResponseEntity.ok(submissionAssembler.modelToDto(submission));
        } catch (ServiceException se) {
            throw new InternalServerException("Could not POST submission!", se);
        }
    }

    @PutMapping("{submissionId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<SubmissionDto> putSubmission(
            @PathVariable(name = "courseId") Long courseId,
            @PathVariable(name = "assignmentId") Long assignmentId,
            @PathVariable(name = "submissionId") Long submissionId,
            @RequestBody @Valid SubmissionGradeDto submissionGradeDto
            ) {
        log.debug("PUT /courses/{}/assignments/{}/submissions/{}", courseId, assignmentId, submissionId);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());

        try {
            Course course = courseService.getById(courseId);

            checkIfParticipates(course, person);

            Assignment assignment = getAssignment(course, assignmentId);

            Submission submission = getSubmission(assignment, submissionId);

            assignment.getSubmissions().forEach((submission1 -> {
                if (submission1.getSubmitter().equals(submission.getSubmitter())) {
                    submission1.setGrade(submissionGradeDto.getGrade());
                }
            }));

            this.courseService.save(course);

            return ResponseEntity.ok(submissionAssembler.modelToDto(submission));
        } catch (ServiceException se) {
            throw new InternalServerException("Could not PUT submission!", se);
        }
    }

    private Assignment getAssignment(Course course, Long assignmentId) {
        return course.getAssignments().stream().filter((assignment1 ->
                assignment1.getId().equals(assignmentId))).findFirst().orElseThrow(() ->
                new NotFoundException("Assignment with id: " + assignmentId + " doesn't exists!"));
    }

    private Submission getSubmission(Assignment assignment, Long submissionId) {
        return assignment.getSubmissions().stream().filter((submission ->
                submission.getId().equals(submissionId))).findFirst().orElseThrow(() ->
                new NotFoundException("Submission with id: " + submissionId + " doesn't exists"));
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
