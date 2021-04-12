package com.bbte.styoudent.api.controller;

import com.bbte.styoudent.api.assembler.AnnouncementAssembler;
import com.bbte.styoudent.api.exception.BadRequestException;
import com.bbte.styoudent.api.exception.NotFoundException;
import com.bbte.styoudent.api.util.AnnouncementUtil;
import com.bbte.styoudent.api.util.ParticipationUtil;
import com.bbte.styoudent.dto.incoming.AnnouncementCreationDto;
import com.bbte.styoudent.dto.outgoing.AnnouncementDto;
import com.bbte.styoudent.model.Announcement;
import com.bbte.styoudent.model.Course;
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
@RequestMapping("/courses/{courseId}/announcements")
public class AnnouncementController {
    private final CourseService courseService;
    private final PersonService personService;
    private final AnnouncementAssembler announcementAssembler;
    private final AnnouncementService announcementService;
    private final ParticipationUtil participationUtil;
    private final AnnouncementUtil announcementUtil;

    public AnnouncementController(CourseService courseService, PersonService personService,
                                  AnnouncementAssembler announcementAssembler,
                                  AnnouncementService announcementService, ParticipationUtil participationUtil,
                                  AnnouncementUtil announcementUtil) {
        this.courseService = courseService;
        this.personService = personService;
        this.announcementAssembler = announcementAssembler;
        this.announcementService = announcementService;
        this.participationUtil = participationUtil;
        this.announcementUtil = announcementUtil;
    }

    @GetMapping(value = "{announcementId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER')")
    public ResponseEntity<AnnouncementDto> getAnnouncement(@PathVariable(name = "courseId") Long courseId,
                                                           @PathVariable(name = "announcementId") Long announcementId) {
        log.debug("GET /courses/{}/announcements/{}", courseId, announcementId);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());
        participationUtil.checkIfParticipates(courseId, person);

        try {
            Announcement announcement = announcementService.getByCourseIdAndId(courseId, announcementId);

            return ResponseEntity.ok(announcementAssembler.modelToDto(announcement));
        } catch (ServiceException se) {
            throw new NotFoundException("Announcement with id:  " + announcementId + " doesn't exists!", se);
        }
    }

    @PostMapping()
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<AnnouncementDto> saveAnnouncement(
            @PathVariable(name = "courseId") Long courseId,
            @RequestBody @Valid AnnouncementCreationDto announcementCreationDto) {
        log.debug("POST /courses/{}/announcements {}", courseId, announcementCreationDto);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());
        participationUtil.checkIfParticipates(courseId, person);

        try {
            Course course = courseService.getById(courseId);

            Announcement announcement = announcementAssembler.creationDtoToModel(announcementCreationDto);
            announcement.setCourse(course);
            announcement.setCreator(person);
            announcement.setDate(LocalDateTime.now());

            announcement = announcementService.save(announcement);

            announcementUtil.createMultipleNotificationsOfAnnouncementCreation(announcement);

            return ResponseEntity.ok(announcementAssembler.modelToDto(announcement));
        } catch (ServiceException se) {
            throw new BadRequestException("Could not POST announcement!", se);
        }
    }

    @PutMapping("{announcementId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<AnnouncementDto> updateAnnouncement(
            @PathVariable(name = "courseId") Long courseId,
            @PathVariable(name = "announcementId") Long announcementId,
            @RequestBody @Valid AnnouncementCreationDto announcementCreationDto) {
        log.debug("PUT /courses/{}/announcements {}", courseId, announcementCreationDto);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());
        participationUtil.checkIfParticipates(courseId, person);

        try {
            Announcement announcement = announcementService.getByCourseIdAndId(courseId, announcementId);
            announcement.setName(announcementCreationDto.getName());
            announcement.setContent(announcementCreationDto.getContent());

            announcementService.save(announcement);

            return ResponseEntity.ok(announcementAssembler.modelToDto(announcement));
        } catch (ServiceException se) {
            throw new BadRequestException("Could not PUT announcement!", se);
        }
    }

    @DeleteMapping("{announcementId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> deleteAnnouncement(@PathVariable(name = "courseId") Long courseId,
                                                @PathVariable(name = "announcementId") Long announcementId) {
        log.debug("DELETE /courses/{}/announcements/{}", courseId, announcementId);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());
        participationUtil.checkIfParticipates(courseId, person);
        announcementUtil.checkIfHasThisAnnouncement(courseId, announcementId);

        try {
           announcementService.delete(announcementId);

            return ResponseEntity.noContent().build();
        } catch (ServiceException se) {
            throw new BadRequestException("Could not DELETE announcement!", se);
        }
    }
}
