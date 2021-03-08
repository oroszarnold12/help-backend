package com.bbte.styoudent.api.controller;

import com.bbte.styoudent.api.assembler.QuizAssembler;
import com.bbte.styoudent.api.exception.BadRequestException;
import com.bbte.styoudent.api.exception.ForbiddenException;
import com.bbte.styoudent.api.exception.InternalServerException;
import com.bbte.styoudent.api.exception.NotFoundException;
import com.bbte.styoudent.dto.incoming.QuizCreationDto;
import com.bbte.styoudent.dto.outgoing.QuizDto;
import com.bbte.styoudent.model.Course;
import com.bbte.styoudent.model.Person;
import com.bbte.styoudent.model.Quiz;
import com.bbte.styoudent.security.util.AuthUtil;
import com.bbte.styoudent.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/courses/{courseId}/quizzes")
public class QuizController {
    private final PersonService personService;
    private final ParticipationService participationService;
    private final CourseService courseService;
    private final QuizAssembler quizAssembler;
    private final QuizService quizService;

    public QuizController(PersonService personService, ParticipationService participationService,
                          CourseService courseService, QuizAssembler quizAssembler, QuizService quizService) {
        this.personService = personService;
        this.participationService = participationService;
        this.courseService = courseService;
        this.quizAssembler = quizAssembler;
        this.quizService = quizService;
    }

    @GetMapping(value = "{quizId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER')")
    public ResponseEntity<QuizDto> getQuiz(@PathVariable(name = "courseId") Long courseId,
                                           @PathVariable(name = "quizId") Long quizId) {
        log.debug("GET /courses/{}/quizzes/{}", courseId, quizId);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());
        checkIfParticipates(courseId, person);

        try {
            Quiz quiz = quizService.getByCourseIdAndId(courseId, quizId);

            return ResponseEntity.ok(quizAssembler.modelToDto(quiz));
        } catch (ServiceException se) {
            throw new NotFoundException("Quiz with id:  " + courseId + " doesn't exists!", se);
        }
    }

    @PostMapping()
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<QuizDto> saveQuiz(
            @PathVariable(name = "courseId") Long courseId,
            @RequestBody @Valid QuizCreationDto quizCreationDto) {
        log.debug("POST /courses/{}/quizzes {}", courseId, quizCreationDto);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());
        checkIfParticipates(courseId, person);

        try {
            Course course = courseService.getById(courseId);

            Quiz quiz = quizAssembler.creationDtoToModel(quizCreationDto);
            quiz.setCourse(course);
            quiz.setPoints(0.0);

            return ResponseEntity.ok(quizAssembler.modelToDto(
                    quizService.save(quiz)
            ));
        } catch (ServiceException se) {
            throw new BadRequestException("Could not POST quiz!", se);
        }
    }

    @PutMapping("{quizId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<QuizDto> updateQuiz(
            @PathVariable(name = "courseId") Long courseId,
            @PathVariable(name = "quizId") Long quizId,
            @RequestBody @Valid QuizCreationDto quizCreationDto) {
        log.debug("PUT /courses/{}/quizzes/ {}", courseId, quizCreationDto);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());
        checkIfParticipates(courseId, person);

        try {
            Quiz quiz = quizService.getByCourseIdAndId(courseId, quizId);

            quiz.setName(quizCreationDto.getName());
            quiz.setDescription(quizCreationDto.getDescription());
            quiz.setDueDate(quizCreationDto.getDueDate());
            quiz.setTimeLimit(quizCreationDto.getTimeLimit());
            quiz.setShowCorrectAnswers(quizCreationDto.getShowCorrectAnswers());
            quiz.setMultipleAttempts(quizCreationDto.getMultipleAttempts());

            quizService.save(quiz);

            return ResponseEntity.ok(quizAssembler.modelToDto(quiz));
        } catch (ServiceException se) {
            throw new BadRequestException("Could not PUT quiz!", se);
        }
    }

    @DeleteMapping("{quizId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> deleteQuiz(@PathVariable(name = "courseId") Long courseId,
                                        @PathVariable(name = "quizId") Long quizId) {
        log.debug("DELETE /courses/{}/quizzes/{}", courseId, quizId);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());
        checkIfParticipates(courseId, person);
        checkIfHasThisQuiz(courseId, quizId);

        try {
            quizService.delete(quizId);

            return ResponseEntity.noContent().build();
        } catch (ServiceException se) {
            throw new BadRequestException("Could not DELETE quiz!", se);
        }
    }

    private void checkIfParticipates(Long courseId, Person person) {
        try {
            if (!participationService.checkIfParticipates(courseId, person)) {
                throw new ForbiddenException("Access denied!");
            }
        } catch (ServiceException se) {
            throw new InternalServerException("Could not check participation!", se);
        }
    }

    private void checkIfHasThisQuiz(Long courseId, Long quizId) {
        try {
            if (!quizService.checkIfExistsByCourseIdAndId(courseId, quizId)) {
                throw new BadRequestException(
                        "Course with id: " + courseId + " has no quiz with id: " + quizId + "!"
                );
            }
        } catch (ServiceException se) {
            throw new InternalServerException("Could not check quiz!", se);
        }
    }
}
