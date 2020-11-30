package com.bbte.styoudent.api.controller;

import com.bbte.styoudent.api.ApiException;
import com.bbte.styoudent.api.assembler.CourseAssembler;
import com.bbte.styoudent.dto.CourseDto;
import com.bbte.styoudent.dto.incoming.CourseCreationDto;
import com.bbte.styoudent.dto.outgoing.ApiResponseMessage;
import com.bbte.styoudent.model.Course;
import com.bbte.styoudent.service.CourseService;
import com.bbte.styoudent.service.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/courses")
public class CourseController {
    private final CourseService courseService;
    private final CourseAssembler courseAssembler;

    public CourseController(CourseService courseService, CourseAssembler courseAssembler) {
        this.courseService = courseService;
        this.courseAssembler = courseAssembler;
    }

    @GetMapping
    public ResponseEntity<Map<String, List<?>>> getCourses() {
        log.debug("GET /patients");

        try {
            return ResponseEntity.ok(Collections.singletonMap("courses",
                    courseService.getAll()
                            .stream()
                            .map(courseAssembler::modelToDto)
                            .collect(Collectors.toList()))
            );
        } catch (ServiceException se) {
            throw new ApiException("Could not GET courses", se);
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<CourseDto> getCourse(@PathVariable(name = "id") Long id) {
        log.debug("GET /patients/{}", id);

        try {
            Course course = courseService.getById(id);
            return ResponseEntity.ok(
                    courseAssembler.modelToDto(course)
            );
        } catch (ServiceException se) {
            throw new ApiException("Could not GET course with id " + id, se);
        }
    }

    @PostMapping
    public ResponseEntity<CourseDto> saveCourse(@RequestBody @Valid CourseCreationDto courseCreationDto) {
        log.debug("POST /courses {}", courseCreationDto);

        try {
            Course course = courseAssembler.courseCreationDtoToModel(courseCreationDto);
            courseService.save(course);
            return ResponseEntity.ok(courseAssembler.modelToDto(course));
        } catch (ServiceException se) {
            throw new ApiException("Could not POST course", se);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseDto> updateCourse(@PathVariable(name = "id") Long id, @RequestBody @Valid CourseDto courseDto, BindingResult errors) {
        log.debug("PUT /courses {}", courseDto);
        if (errors.hasErrors()) {
            throw new ApiException(errors
                    .getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining()));
        }

        if (!id.equals(courseDto.getId())) {
            throw new ApiException("URI and Object ids do not match");
        }

        try {
            Course course = courseService.getById(id);
            courseAssembler.updateCourseFromDto(courseDto, course);
            log.info("{}", course.getAssignments().get(0).getCourse());
            courseService.save(course);
            return ResponseEntity.ok(courseAssembler.modelToDto(course));
        } catch (ServiceException se) {
            throw new ApiException("Could not PUT course with id: " + id, se);
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponseMessage> deleteCourse(@PathVariable(name = "id") Long id) {
        log.debug("DELETE /courses/{}", id);

        try {
            courseService.delete(id);
            return ResponseEntity.ok().body(new ApiResponseMessage("Course deletion with id " + id + " successful."));
        } catch (ServiceException se) {
            throw new ApiException("Could not DELETE course with id " + id, se);
        }
    }
}
