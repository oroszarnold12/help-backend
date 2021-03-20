package com.bbte.styoudent.api.controller;

import com.bbte.styoudent.api.assembler.DiscussionAssembler;
import com.bbte.styoudent.api.exception.BadRequestException;
import com.bbte.styoudent.api.exception.ForbiddenException;
import com.bbte.styoudent.api.exception.NotFoundException;
import com.bbte.styoudent.api.util.DiscussionUtil;
import com.bbte.styoudent.api.util.ParticipationUtil;
import com.bbte.styoudent.dto.incoming.DiscussionCreationDto;
import com.bbte.styoudent.dto.outgoing.DiscussionDto;
import com.bbte.styoudent.model.Course;
import com.bbte.styoudent.model.Discussion;
import com.bbte.styoudent.model.Person;
import com.bbte.styoudent.model.Role;
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
@RequestMapping("/courses/{courseId}/discussions")
public class DiscussionController {
    private final CourseService courseService;
    private final PersonService personService;
    private final DiscussionAssembler discussionAssembler;
    private final DiscussionService discussionService;
    private final DiscussionUtil discussionUtil;
    private final ParticipationUtil participationUtil;

    public DiscussionController(CourseService courseService, PersonService personService,
                                DiscussionAssembler discussionAssembler,
                                DiscussionService discussionService, DiscussionUtil discussionUtil,
                                ParticipationUtil participationUtil) {
        this.courseService = courseService;
        this.personService = personService;
        this.discussionAssembler = discussionAssembler;
        this.discussionService = discussionService;
        this.discussionUtil = discussionUtil;
        this.participationUtil = participationUtil;
    }

    @GetMapping(value = "{discussionId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER')")
    public ResponseEntity<DiscussionDto> getDiscussions(@PathVariable(name = "courseId") Long courseId,
                                                        @PathVariable(name = "discussionId") Long discussionId) {
        log.debug("GET /courses/{}/discussions/{}", courseId, discussionId);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());
        participationUtil.checkIfParticipates(courseId, person);

        try {
            Discussion discussion = discussionService.getByCourseIdAndId(courseId, discussionId);

            return ResponseEntity.ok(discussionAssembler.modelToDto(discussion));
        } catch (ServiceException se) {
            throw new NotFoundException("Discussion with id:  " + courseId + " doesn't exists!", se);
        }
    }

    @PostMapping()
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER')")
    public ResponseEntity<DiscussionDto> saveDiscussions(
            @PathVariable(name = "courseId") Long courseId,
            @RequestBody @Valid DiscussionCreationDto discussionCreationDto) {
        log.debug("POST /courses/{}/discussions {}", courseId, discussionCreationDto);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());
        participationUtil.checkIfParticipates(courseId, person);

        try {
            Course course = courseService.getById(courseId);

            Discussion discussion = discussionAssembler.creationDtoToModel(discussionCreationDto);
            discussion.setCourse(course);
            discussion.setDate(LocalDateTime.now());
            discussion.setCreator(person);

            return ResponseEntity.ok(discussionAssembler.modelToDto(
                    discussionService.save(discussion)
            ));
        } catch (ServiceException se) {
            throw new BadRequestException("Could not POST discussion!", se);
        }
    }

    @PutMapping("{discussionId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER')")
    public ResponseEntity<DiscussionDto> updateAnnouncement(
            @PathVariable(name = "courseId") Long courseId,
            @PathVariable(name = "discussionId") Long discussionId,
            @RequestBody @Valid DiscussionCreationDto discussionCreationDto) {
        log.debug("PUT /courses/{}/announcements {}", courseId, discussionCreationDto);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());

        try {
            Discussion discussion = discussionService.getByCourseIdAndId(courseId, discussionId);

            if (!discussion.getCreator().equals(person) && !person.getRole().equals(Role.ROLE_TEACHER)) {
                throw new ForbiddenException("Access denied!");
            }

            discussion.setName(discussionCreationDto.getName());
            discussion.setContent(discussionCreationDto.getContent());

            discussionService.save(discussion);

            return ResponseEntity.ok(discussionAssembler.modelToDto(discussion));
        } catch (ServiceException se) {
            throw new BadRequestException("Could not PUT discussion!", se);
        }
    }

    @DeleteMapping("{discussionId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER')")
    public ResponseEntity<?> deleteDiscussion(@PathVariable(name = "courseId") Long courseId,
                                              @PathVariable(name = "discussionId") Long discussionId) {
        log.debug("DELETE /courses/{}/discussions/{}", courseId, discussionId);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());
        discussionUtil.checkIfHasThisDiscussion(courseId, discussionId);

        try {
            Discussion discussion = discussionService.getByCourseIdAndId(courseId, discussionId);

            if (!discussion.getCreator().equals(person) && !person.getRole().equals(Role.ROLE_TEACHER)) {
                throw new ForbiddenException("Access denied!");
            }

            discussionService.delete(discussionId);

            return ResponseEntity.noContent().build();
        } catch (ServiceException se) {
            throw new BadRequestException("Could not DELETE discussion!", se);
        }
    }
}
