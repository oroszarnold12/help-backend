package com.bbte.styoudent.api.controller;

import com.bbte.styoudent.api.assembler.QuizSubmissionAssembler;
import com.bbte.styoudent.api.exception.BadRequestException;
import com.bbte.styoudent.api.exception.ForbiddenException;
import com.bbte.styoudent.api.exception.InternalServerException;
import com.bbte.styoudent.dto.incoming.QuizSubmissionCreationDto;
import com.bbte.styoudent.dto.outgoing.QuizSubmissionDto;
import com.bbte.styoudent.model.*;
import com.bbte.styoudent.security.util.AuthUtil;
import com.bbte.styoudent.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/courses/{courseId}/quizzes/{quizId}/submissions")
public class QuizSubmissionController {
    private final PersonService personService;
    private final QuizService quizService;
    private final ParticipationService participationService;
    private final QuizSubmissionService quizSubmissionService;
    private final QuizSubmissionAssembler quizSubmissionAssembler;

    public QuizSubmissionController(PersonService personService, QuizService quizService,
                                    ParticipationService participationService,
                                    QuizSubmissionService quizSubmissionService,
                                    QuizSubmissionAssembler quizSubmissionAssembler) {
        this.personService = personService;
        this.quizService = quizService;
        this.participationService = participationService;
        this.quizSubmissionService = quizSubmissionService;
        this.quizSubmissionAssembler = quizSubmissionAssembler;
    }

    @GetMapping
    @PreAuthorize("hasRole('TEACHER') or hasRole('STUDENT')")
    ResponseEntity<List<?>> getQuestions(
            @PathVariable(name = "courseId") Long courseId,
            @PathVariable(name = "quizId") Long quizId
    ) {
        log.debug("GET /courses/{}/quizzes/{}/submissions", courseId, quizId);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());
        checkIfParticipates(courseId, person);
        checkIfHasThisQuiz(courseId, quizId);

        try {
            Quiz quiz = quizService.getByCourseIdAndId(courseId, quizId);

            if (person.getRole().equals(Role.ROLE_STUDENT) && !quiz.getShowCorrectAnswers()) {
                return ResponseEntity.ok(
                        quizSubmissionService.getAllByQuizIdAndSubmitterId(quizId, person.getId())
                                .stream().map(quizSubmissionAssembler::modelToThinDto)
                                .collect(Collectors.toList())
                );
            } else {
                return ResponseEntity.ok(
                        quizSubmissionService.getAllByQuizIdAndSubmitterId(quizId, person.getId())
                                .stream().map(quizSubmissionAssembler::modelToDto)
                                .collect(Collectors.toList())
                );
            }
        } catch (InternalServerException se) {
            throw new BadRequestException("Could not GET quiz submissions!", se);
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('TEACHER') or hasRole('STUDENT')")
    ResponseEntity<QuizSubmissionDto> saveQuizSubmission(
            @PathVariable(name = "courseId") Long courseId,
            @PathVariable(name = "quizId") Long quizId,
            @RequestBody @Valid @Size(min = 2) List<QuizSubmissionCreationDto> quizSubmissionCreationDtos
    ) {
        log.debug("POST /courses/{}/quizzes/{}/submissions", courseId, quizId);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());
        checkIfParticipates(courseId, person);

        try {
            Quiz quiz = quizService.getByCourseIdAndId(courseId, quizId);
            if (!quiz.getMultipleAttempts()) {
                checkIfAlreadySubmitted(quizId, person.getId());
            }

            QuizSubmission quizSubmission = new QuizSubmission();
            quizSubmission.setQuiz(quiz);
            quizSubmission.setSubmitter(person);
            List<AnswerSubmission> answerSubmissions = new ArrayList<>();

            quizSubmissionCreationDtos.forEach((quizSubmissionCreationDto -> {
                AnswerSubmission answerSubmission = new AnswerSubmission();
                answerSubmission.setPicked(quizSubmissionCreationDto.getPicked());
                answerSubmission.setAnswer(getAnswer(quiz, quizSubmissionCreationDto.getAnswerId()));
                answerSubmission.setQuizSubmission(quizSubmission);
                answerSubmissions.add(answerSubmission);
            }));

            quizSubmission.setAnswers(answerSubmissions);

            return ResponseEntity.ok(
                    quizSubmissionAssembler.modelToDto(quizSubmissionService.save(quizSubmission))
            );
        } catch (ServiceException se) {
            throw new BadRequestException("Could not POST quiz submission!", se);
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

    private void checkIfAlreadySubmitted(Long quizId, Long submitterId) {
        try {
            if (quizSubmissionService.checkIfExistsByQuizIdAndSubmitterId(quizId, submitterId)) {
                throw new BadRequestException("You have already submitted this quiz!");
            }
        } catch (ServiceException se) {
            throw new InternalServerException("Could not check quiz submission!", se);
        }
    }

    private Answer getAnswer(Quiz quiz, Long answerId) {
        List<Answer> answers = new ArrayList<>();
        quiz.getQuestions().forEach(question -> {
            answers.addAll(question.getAnswers());
        });

        return answers.stream().filter(answer -> answer.getId().equals(answerId)).findFirst().orElseThrow(() ->
                new BadRequestException(
                        "Quiz with id: " + quiz.getId() + " has no answer with id: " + answerId
                ));
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
