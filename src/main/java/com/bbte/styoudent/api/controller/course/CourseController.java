package com.bbte.styoudent.api.controller.course;

import com.bbte.styoudent.api.assembler.CourseAssembler;
import com.bbte.styoudent.api.exception.BadRequestException;
import com.bbte.styoudent.api.exception.ForbiddenException;
import com.bbte.styoudent.api.exception.InternalServerException;
import com.bbte.styoudent.api.exception.NotFoundException;
import com.bbte.styoudent.dto.incoming.course.CourseCreationDto;
import com.bbte.styoudent.dto.outgoing.ApiResponseMessage;
import com.bbte.styoudent.dto.outgoing.course.CourseDto;
import com.bbte.styoudent.dto.outgoing.course.ThinCourseDto;
import com.bbte.styoudent.model.assignment.Assignment;
import com.bbte.styoudent.model.course.Course;
import com.bbte.styoudent.model.person.Person;
import com.bbte.styoudent.model.person.Role;
import com.bbte.styoudent.model.quiz.Quiz;
import com.bbte.styoudent.security.util.AuthUtil;
import com.bbte.styoudent.service.course.CourseService;
import com.bbte.styoudent.service.person.ParticipationService;
import com.bbte.styoudent.service.person.PersonService;
import com.bbte.styoudent.service.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/courses")
public class CourseController {
    private final CourseService courseService;
    private final CourseAssembler courseAssembler;
    private final PersonService personService;
    private final ParticipationService participationService;

    public CourseController(CourseService courseService, CourseAssembler courseAssembler,
                            PersonService personService, ParticipationService participationService) {
        this.courseService = courseService;
        this.courseAssembler = courseAssembler;
        this.personService = personService;
        this.participationService = participationService;
    }

    @GetMapping
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, List<ThinCourseDto>>> getCourses() {
        log.debug("GET /courses");

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());

        try {
            return ResponseEntity.ok(Collections.singletonMap("courses",
                    courseService.getAllCoursesByPerson(person)
                            .stream()
                            .map(courseAssembler::modelToThinDto)
                            .collect(Collectors.toList()))
            );
        } catch (ServiceException se) {
            throw new InternalServerException("Could not GET courses!", se);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER')")
    public ResponseEntity<CourseDto> getCourse(@PathVariable(name = "id") Long id) {
        log.debug("GET /courses/{}", id);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());

        try {
            Course course = courseService.getCourseByPerson(person, id);

            if (person.getRole().equals(Role.ROLE_STUDENT)) {
                course.setAssignments(course.getAssignments().stream().filter(Assignment::getPublished)
                        .collect(Collectors.toList()));

                course.setQuizzes(course.getQuizzes().stream().filter(Quiz::getPublished)
                        .collect(Collectors.toList()));
            }

            return ResponseEntity.ok(
                    courseAssembler.modelToDto(course)
            );
        } catch (ServiceException se) {
            throw new NotFoundException("Course with id:  " + id + " doesn't exists!", se);
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<CourseDto> saveCourse(@RequestBody @Valid CourseCreationDto courseCreationDto) {
        log.debug("POST /courses {}", courseCreationDto);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());

        try {
            Course course = courseAssembler.creationDtoToModel(courseCreationDto);
            course.setTeacher(person);
            courseService.save(course);
            participationService.createInitialParticipation(course, person);

            return ResponseEntity.ok(courseAssembler.modelToDto(course));
        } catch (ServiceException se) {
            throw new BadRequestException("Could not POST course!", se);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<CourseDto> updateCourse(@PathVariable(name = "id") Long id,
                                                  @RequestBody @Valid CourseCreationDto courseCreationDto) {
        log.debug("PUT /courses {}", courseCreationDto);

        try {
            Course course = courseService.getById(id);
            course.setName(courseCreationDto.getName());
            course.setLongName(courseCreationDto.getLongName());
            course.setDescription(courseCreationDto.getDescription());
            courseService.save(course);

            return ResponseEntity.ok(courseAssembler.modelToDto(course));
        } catch (ServiceException se) {
            throw new BadRequestException("Could not PUT course with id: " + id + "!", se);
        }
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponseMessage> deleteCourse(@PathVariable(name = "id") Long id) {
        log.debug("DELETE /courses/{}", id);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());

        try {
            Long teacherId = courseService.getById(id).getTeacher().getId();

            if (!teacherId.equals(person.getId())) {
                throw new ForbiddenException("Access denied!");
            }
        } catch (ServiceException se) {
            throw new BadRequestException("Course with id: " + id + " doesn't exists!", se);
        }

        try {
            courseService.delete(id);

            return ResponseEntity.noContent().build();
        } catch (ServiceException se) {
            throw new BadRequestException("Could not DELETE course with id " + id + "!", se);
        }
    }
}
