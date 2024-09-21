package com.help.api.controller.discussion;

import com.help.api.assembler.DiscussionAssembler;
import com.help.api.exception.BadRequestException;
import com.help.api.exception.ForbiddenException;
import com.help.api.exception.NotFoundException;
import com.help.api.util.DiscussionUtil;
import com.help.api.util.ParticipationUtil;
import com.help.dto.incoming.discussion.DiscussionCreationDto;
import com.help.dto.outgoing.discussion.DiscussionDto;
import com.help.model.course.Course;
import com.help.model.discussion.Discussion;
import com.help.model.person.Person;
import com.help.model.person.Role;
import com.help.security.util.AuthUtil;
import com.help.service.ServiceException;
import com.help.service.course.CourseService;
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

    @GetMapping("{discussionId}")
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

            discussion = discussionService.save(discussion);

            discussionUtil.createMultipleNotificationsOfDiscussionCreation(discussion);

            return ResponseEntity.ok(discussionAssembler.modelToDto(discussion));
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
