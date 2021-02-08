package com.bbte.styoudent.api.controller;

import com.bbte.styoudent.api.assembler.DiscussionAssembler;
import com.bbte.styoudent.api.exception.BadRequestException;
import com.bbte.styoudent.api.exception.NotFoundException;
import com.bbte.styoudent.dto.incoming.DiscussionCreationDto;
import com.bbte.styoudent.dto.outgoing.DiscussionDto;
import com.bbte.styoudent.model.Course;
import com.bbte.styoudent.model.Discussion;
import com.bbte.styoudent.model.Person;
import com.bbte.styoudent.security.util.AuthUtil;
import com.bbte.styoudent.service.CourseService;
import com.bbte.styoudent.service.PersonService;
import com.bbte.styoudent.service.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/courses/{courseId}/discussions")
public class DiscussionController {
    private final CourseService courseService;
    private final PersonService personService;
    private final DiscussionAssembler discussionAssembler;

    public DiscussionController(CourseService courseService, PersonService personService,
                                DiscussionAssembler discussionAssembler) {
        this.courseService = courseService;
        this.personService = personService;
        this.discussionAssembler = discussionAssembler;
    }

    @GetMapping(value = "{discussionId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER')")
    public ResponseEntity<DiscussionDto> getDiscussions(@PathVariable(name = "courseId") Long courseId,
                                                        @PathVariable(name = "discussionId") Long discussionId) {
        log.debug("GET /courses/{}/discussions/{}", courseId, discussionId);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());

        try {
            Course course = courseService.getCourseByPerson(person, courseId);

            Discussion discussion = course.getDiscussions().stream().filter((discussion1 ->
                    discussion1.getId().equals(discussionId))).findFirst().orElseThrow(() ->
                    new NotFoundException("Discussion with id: " + discussionId + " doesn't exists!"));

            return ResponseEntity.ok(discussionAssembler.modelToDto(discussion));
        } catch (ServiceException se) {
            throw new NotFoundException("Course with id:  " + courseId + " doesn't exists!", se);
        }
    }

    @PostMapping()
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<DiscussionDto> saveDiscussions(
            @PathVariable(name = "courseId") Long courseId,
            @RequestBody @Valid DiscussionCreationDto discussionCreationDto) {
        log.debug("POST /courses/{}/discussions {}", courseId, discussionCreationDto);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());

        try {
            Course course = courseService.getById(courseId);

            Discussion discussion = discussionAssembler.creationDtoToModel(discussionCreationDto);
            discussion.setCourse(course);
            discussion.setDate(LocalDateTime.now());
            discussion.setCreator(person);

            course.getDiscussions().add(discussion);
            courseService.save(course);

            return ResponseEntity.ok(discussionAssembler.modelToDto(discussion));
        } catch (ServiceException se) {
            throw new BadRequestException("Could not POST discussion!", se);
        }
    }

    @PutMapping("{discussionId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<DiscussionDto> updateAnnouncement(
            @PathVariable(name = "courseId") Long courseId,
            @PathVariable(name = "discussionId") Long discussionId,
            @RequestBody @Valid DiscussionCreationDto discussionCreationDto) {
        log.debug("PUT /courses/{}/announcements {}", courseId, discussionCreationDto);

        try {
            Course course = courseService.getById(courseId);

            Discussion discussion = course.getDiscussions().stream().filter((discussion1 ->
                    discussion1.getId().equals(discussionId))).findFirst().orElseThrow(() ->
                    new NotFoundException("Discussion with id: " + discussionId + " doesn't exists!"));
            discussion.setName(discussionCreationDto.getName());
            discussion.setContent(discussionCreationDto.getContent());

            courseService.save(course);

            return ResponseEntity.ok(discussionAssembler.modelToDto(discussion));
        } catch (ServiceException se) {
            throw new BadRequestException("Could not PUT discussion!", se);
        }
    }

    @DeleteMapping("{discussionId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> deleteDiscussion(@PathVariable(name = "courseId") Long courseId,
                                              @PathVariable(name = "discussionId") Long discussionId) {
        log.debug("DELETE /courses/{}/discussions/{}", courseId, discussionId);

        try {
            Course course = courseService.getById(courseId);

            Discussion discussion = course.getDiscussions().stream().filter((discussion1 ->
                    discussion1.getId().equals(discussionId))).findFirst().orElseThrow(() ->
                    new NotFoundException("Discussion with id: " + discussionId + " doesn't exists!"));

            course.getDiscussions().remove(discussion);

            courseService.save(course);

            return ResponseEntity.noContent().build();
        } catch (ServiceException se) {
            throw new BadRequestException("Could not DELETE discussion!", se);
        }
    }
}
