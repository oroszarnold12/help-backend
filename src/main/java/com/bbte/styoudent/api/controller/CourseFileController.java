package com.bbte.styoudent.api.controller;

import com.bbte.styoudent.api.assembler.CourseAssembler;
import com.bbte.styoudent.api.exception.BadRequestException;
import com.bbte.styoudent.api.exception.InternalServerException;
import com.bbte.styoudent.api.util.CourseUtil;
import com.bbte.styoudent.api.util.ParticipationUtil;
import com.bbte.styoudent.dto.outgoing.CourseFileDto;
import com.bbte.styoudent.model.Course;
import com.bbte.styoudent.model.CourseFile;
import com.bbte.styoudent.model.CourseFileObject;
import com.bbte.styoudent.model.Person;
import com.bbte.styoudent.security.util.AuthUtil;
import com.bbte.styoudent.service.*;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/courses/{courseId}/files")
public class CourseFileController {
    private final ParticipationUtil participationUtil;
    private final PersonService personService;
    private final CourseService courseService;
    private final CourseFileService courseFileService;
    private final CourseAssembler courseAssembler;
    private final ZipService zipService;
    private final CourseUtil courseUtil;

    public CourseFileController(ParticipationUtil participationUtil, PersonService personService,
                                CourseService courseService, CourseFileService courseFileRepository,
                                CourseAssembler courseAssembler, ZipService zipService, CourseUtil courseUtil) {
        this.participationUtil = participationUtil;
        this.personService = personService;
        this.courseService = courseService;
        this.courseFileService = courseFileRepository;
        this.courseAssembler = courseAssembler;
        this.zipService = zipService;
        this.courseUtil = courseUtil;
    }

    @Transactional
    @GetMapping("{fileId}")
    @PreAuthorize("hasRole('TEACHER') or hasRole('STUDENT')")
    public ResponseEntity<Resource> saveCourseFile(
            @PathVariable(name = "courseId") Long courseId,
            @PathVariable(name = "fileId") Long fileId
    ) {
        log.debug("GET /courses/{}/files/{}", courseId, fileId);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());
        participationUtil.checkIfParticipates(courseId, person);

        try {
            CourseFile courseFile = courseFileService.getByCourseIdAndId(courseId, fileId);

            return ResponseEntity.ok()
                    .body(
                            new ByteArrayResource(courseFile.getCourseFileObject().getBytes())
                    );
        } catch (ServiceException se) {
            throw new BadRequestException("Course with id: " + courseId + " has no file with id:" + fileId + "!", se);
        }
    }

    @Transactional
    @GetMapping()
    @PreAuthorize("hasRole('TEACHER') or hasRole('STUDENT')")
    public ResponseEntity<Resource> getAllCourseFiles(
            @PathVariable(name = "courseId") Long courseId,
            @RequestHeader(name = "courseFilesIds", required = false) List<String> courseFilesIds
    ) {
        log.debug("GET /courses/{}/files", courseId);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());
        participationUtil.checkIfParticipates(courseId, person);

        try {
            List<CourseFile> courseFiles = courseFileService.getByCourseId(courseId);

            if (courseFilesIds != null) {
                courseFiles = courseFiles.stream().filter((courseFile ->
                        courseFilesIds.contains(courseFile.getId().toString()))).collect(Collectors.toList());
            }

            zipService.open();

            courseFiles.forEach(courseFile ->
            {
                String fileName = courseFile.getFileName()
                        .replaceAll("\\s+", "_");

                zipService.write(fileName, courseFile.getCourseFileObject().getBytes());
            });

            zipService.close();

            return ResponseEntity.ok()
                    .body(
                            new ByteArrayResource(zipService.getBytes())
                    );
        } catch (ServiceException se) {
            throw new InternalServerException("Could not GET files of course!", se);
        }
    }

    @Transactional
    @PostMapping()
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<CourseFileDto>> saveSubmission(
            @PathVariable(name = "courseId") Long courseId,
            @RequestParam("files") @Size(min = 1, max = 5) MultipartFile[] files
    ) {
        log.debug("POST /courses/{}/files", courseId);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());
        participationUtil.checkIfParticipates(courseId, person);

        List<CourseFileDto> courseFileDtos = new ArrayList<>();

        try {
            Course course = courseService.getById(courseId);

            for (MultipartFile file : files) {
                CourseFile courseFile = new CourseFile();
                courseFile.setCourse(course);
                courseFile.setSize(file.getSize());
                courseFile.setCreationDate(LocalDateTime.now());
                courseFile.setFileName(CourseUtil.getCorrectedFileName(
                        course.getFiles(),
                        StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()))
                ));
                courseFile.setUploader(person);

                try {
                    CourseFileObject courseFileObject = new CourseFileObject();
                    courseFileObject.setBytes(file.getBytes());
                    courseFile.setCourseFileObject(courseFileObject);
                } catch (IOException ioException) {
                    throw new InternalServerException("Could not process file " + file.getOriginalFilename());
                }

                try {
                    courseFileDtos.add(courseAssembler.modelToDto(courseFileService.save(courseFile)));
                } catch (ServiceException se) {
                    throw new InternalServerException("Could not POST file with name: " + courseFile.getFileName());
                }
            }

            return ResponseEntity.ok(courseFileDtos);
        } catch (ServiceException se) {
            throw new BadRequestException("Course with id: " + courseId + " doesn't exists!", se);
        }
    }

    @DeleteMapping("{fileId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> deleteCourseFile(
            @PathVariable(name = "courseId") Long courseId,
            @PathVariable(name = "fileId") Long fileId
    ) {
        log.debug("DELETE /courses/{}/files/{}", courseId, fileId);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());
        participationUtil.checkIfParticipates(courseId, person);
        courseUtil.checkIfHasThisFile(courseId, fileId);

        try {
            courseFileService.deleteById(fileId);

            return ResponseEntity.noContent().build();
        } catch (ServiceException se) {
            throw new InternalServerException("Could not delete file with id: " + fileId + "!", se);
        }
    }
}
