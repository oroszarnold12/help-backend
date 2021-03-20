package com.bbte.styoudent.api.controller;

import com.bbte.styoudent.api.assembler.QuestionAssembler;
import com.bbte.styoudent.api.exception.BadRequestException;
import com.bbte.styoudent.api.exception.InternalServerException;
import com.bbte.styoudent.api.util.ParticipationUtil;
import com.bbte.styoudent.api.util.QuizUtil;
import com.bbte.styoudent.dto.incoming.QuestionCreationDto;
import com.bbte.styoudent.dto.outgoing.QuestionDto;
import com.bbte.styoudent.model.Person;
import com.bbte.styoudent.model.Question;
import com.bbte.styoudent.model.Quiz;
import com.bbte.styoudent.security.util.AuthUtil;
import com.bbte.styoudent.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/courses/{courseId}/quizzes/{quizId}/questions")
public class QuestionController {
    private final PersonService personService;
    private final QuizService quizService;
    private final QuestionAssembler questionAssembler;
    private final QuestionService questionService;
    private final ParticipationUtil participationUtil;
    private final QuizUtil quizUtil;

    public QuestionController(PersonService personService,
                              QuizService quizService, QuestionAssembler questionAssembler,
                              QuestionService questionService, ParticipationUtil participationUtil, QuizUtil quizUtil) {
        this.personService = personService;
        this.quizService = quizService;
        this.questionAssembler = questionAssembler;
        this.questionService = questionService;
        this.participationUtil = participationUtil;
        this.quizUtil = quizUtil;
    }

    @GetMapping
    @PreAuthorize("hasRole('TEACHER') or hasRole('STUDENT')")
    ResponseEntity<List<QuestionDto>> getQuestions(
            @PathVariable(name = "courseId") Long courseId,
            @PathVariable(name = "quizId") Long quizId
    ) {
        log.debug("GET /courses/{}/quizzes/{}/questions", courseId, quizId);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());
        participationUtil.checkIfParticipates(courseId, person);
        quizUtil.checkIfHasThisQuiz(courseId, quizId);

        try {
            return ResponseEntity.ok(
                    questionService.getAllByQuizId(quizId)
                            .stream().map(questionAssembler::modelToDto)
                            .collect(Collectors.toList())
            );
        } catch (InternalServerException se) {
            throw new BadRequestException("Could not GET questions!", se);
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    ResponseEntity<QuestionDto> saveQuestion(
            @PathVariable(name = "courseId") Long courseId,
            @PathVariable(name = "quizId") Long quizId,
            @RequestBody @Valid QuestionCreationDto questionCreationDto
    ) {
        log.debug("POST /courses/{}/quizzes/{}/questions", courseId, quizId);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());
        participationUtil.checkIfParticipates(courseId, person);

        try {
            Quiz quiz = quizService.getByCourseIdAndId(courseId, quizId);

            Question question = questionAssembler.creationDtoToModel(questionCreationDto);
            question.setQuiz(quiz);

            question.getAnswers().forEach((answer -> answer.setQuestion(question)));

            quiz.setPoints(quiz.getPoints() + question.getPoints());

            return ResponseEntity.ok(
                    questionAssembler.modelToDto(questionService.save(question))
            );
        } catch (ServiceException se) {
            throw new BadRequestException("Could not POST question!", se);
        }
    }

    @PutMapping("{questionId}")
    @PreAuthorize("hasRole('TEACHER')")
    ResponseEntity<QuestionDto> updateQuestion(
            @PathVariable(name = "courseId") Long courseId,
            @PathVariable(name = "quizId") Long quizId,
            @PathVariable(name = "questionId") Long questionId,
            @RequestBody @Valid QuestionCreationDto questionCreationDto
    ) {
        log.debug("PUT /courses/{}/quizzes/{}/questions/{}", courseId, quizId, questionId);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());
        participationUtil.checkIfParticipates(courseId, person);

        try {
            Quiz quiz = quizService.getByCourseIdAndId(courseId, quizId);

            Question question = questionAssembler.creationDtoToModel(questionCreationDto);
            question.setId(questionId);
            question.setQuiz(quiz);
            question.getAnswers().forEach((answer -> answer.setQuestion(question)));

            try {
                Question question1 = questionService.getByQuizIdAndId(quizId, questionId);
                quiz.setPoints(quiz.getPoints() + (question.getPoints() - question1.getPoints()));
            } catch (ServiceException se) {
                quiz.setPoints(quiz.getPoints() + question.getPoints());
            }

            return ResponseEntity.ok(
                    questionAssembler.modelToDto(questionService.save(question))
            );
        } catch (ServiceException se) {
            throw new BadRequestException("Could not PUT question!", se);
        }
    }

    @DeleteMapping("{questionId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> deleteQuestion(@PathVariable(name = "courseId") Long courseId,
                                            @PathVariable(name = "quizId") Long quizId,
                                            @PathVariable(name = "questionId") Long questionId) {
        log.debug("DELETE /courses/{}/quizzes/{}/questions/{}", courseId, quizId, questionId);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());
        participationUtil.checkIfParticipates(courseId, person);

        try {
            Quiz quiz = quizService.getByCourseIdAndId(courseId, quizId);
            Question question = questionService.getByQuizIdAndId(quizId, questionId);
            quiz.setPoints(quiz.getPoints() - question.getPoints());

            questionService.delete(questionId);

            return ResponseEntity.noContent().build();
        } catch (ServiceException se) {
            throw new BadRequestException("Could not DELETE question!", se);
        }
    }
}
