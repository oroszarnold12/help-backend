package com.help.api.controller.quiz;

import com.help.api.assembler.QuizAssembler;
import com.help.api.exception.BadRequestException;
import com.help.api.exception.InternalServerException;
import com.help.api.util.ParticipationUtil;
import com.help.api.util.QuizUtil;
import com.help.dto.incoming.quiz.QuestionCreationDto;
import com.help.dto.outgoing.quiz.QuestionDto;
import com.help.model.person.Person;
import com.help.model.quiz.Question;
import com.help.model.quiz.Quiz;
import com.help.security.util.AuthUtil;
import com.help.service.ServiceException;
import com.help.service.person.PersonService;
import com.help.service.quiz.QuestionService;
import com.help.service.quiz.QuizService;
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
    private final QuizAssembler quizAssembler;
    private final QuestionService questionService;
    private final ParticipationUtil participationUtil;
    private final QuizUtil quizUtil;

    public QuestionController(PersonService personService,
                              QuizService quizService, QuizAssembler quizAssembler,
                              QuestionService questionService, ParticipationUtil participationUtil, QuizUtil quizUtil) {
        this.personService = personService;
        this.quizService = quizService;
        this.quizAssembler = quizAssembler;
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
                            .stream().map(quizAssembler::modelToDto)
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

            Question question = quizAssembler.creationDtoToModel(questionCreationDto);
            question.setQuiz(quiz);

            question.getAnswers().forEach(answer -> answer.setQuestion(question));

            quiz.setPoints(quiz.getPoints() + question.getPoints());

            return ResponseEntity.ok(
                    quizAssembler.modelToDto(questionService.save(question))
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

            Question question = quizAssembler.creationDtoToModel(questionCreationDto);
            question.setId(questionId);
            question.setQuiz(quiz);
            question.getAnswers().forEach(answer -> answer.setQuestion(question));

            try {
                Question question1 = questionService.getByQuizIdAndId(quizId, questionId);
                quiz.setPoints(quiz.getPoints() + (question.getPoints() - question1.getPoints()));
            } catch (ServiceException se) {
                quiz.setPoints(quiz.getPoints() + question.getPoints());
            }

            return ResponseEntity.ok(
                    quizAssembler.modelToDto(questionService.save(question))
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
