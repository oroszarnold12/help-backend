package com.bbte.styoudent.api.controller;

import com.bbte.styoudent.api.assembler.QuizSubmissionAssembler;
import com.bbte.styoudent.api.exception.BadRequestException;
import com.bbte.styoudent.api.exception.InternalServerException;
import com.bbte.styoudent.api.util.ParticipationUtil;
import com.bbte.styoudent.api.util.QuizUtil;
import com.bbte.styoudent.dto.incoming.QuizSubmissionCreationDto;
import com.bbte.styoudent.dto.outgoing.QuizSubmissionDto;
import com.bbte.styoudent.model.*;
import com.bbte.styoudent.security.util.AuthUtil;
import com.bbte.styoudent.service.PersonService;
import com.bbte.styoudent.service.QuizService;
import com.bbte.styoudent.service.QuizSubmissionService;
import com.bbte.styoudent.service.ServiceException;
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
    private final QuizSubmissionService quizSubmissionService;
    private final QuizSubmissionAssembler quizSubmissionAssembler;
    private final ParticipationUtil participationUtil;
    private final QuizUtil quizUtil;

    public QuizSubmissionController(PersonService personService, QuizService quizService,
                                    QuizSubmissionService quizSubmissionService,
                                    QuizSubmissionAssembler quizSubmissionAssembler,
                                    ParticipationUtil participationUtil, QuizUtil quizUtil) {
        this.personService = personService;
        this.quizService = quizService;
        this.quizSubmissionService = quizSubmissionService;
        this.quizSubmissionAssembler = quizSubmissionAssembler;
        this.participationUtil = participationUtil;
        this.quizUtil = quizUtil;
    }

    @GetMapping
    @PreAuthorize("hasRole('TEACHER') or hasRole('STUDENT')")
    ResponseEntity<List<?>> getQuestions(
            @PathVariable(name = "courseId") Long courseId,
            @PathVariable(name = "quizId") Long quizId
    ) {
        log.debug("GET /courses/{}/quizzes/{}/submissions", courseId, quizId);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());
        participationUtil.checkIfParticipates(courseId, person);
        quizUtil.checkIfHasThisQuiz(courseId, quizId);

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
        participationUtil.checkIfParticipates(courseId, person);

        try {
            Quiz quiz = quizService.getByCourseIdAndId(courseId, quizId);
            if (!quiz.getMultipleAttempts()) {
                quizUtil.checkIfAlreadySubmitted(quizId, person.getId());
            }

            QuizSubmission quizSubmission = new QuizSubmission();
            quizSubmission.setQuiz(quiz);
            quizSubmission.setSubmitter(person);
            List<AnswerSubmission> answerSubmissions = new ArrayList<>();

            quizSubmissionCreationDtos.forEach((quizSubmissionCreationDto -> {
                AnswerSubmission answerSubmission = new AnswerSubmission();
                answerSubmission.setPicked(quizSubmissionCreationDto.getPicked());
                answerSubmission.setAnswer(quizUtil.getAnswer(quiz, quizSubmissionCreationDto.getAnswerId()));
                answerSubmission.setQuizSubmission(quizSubmission);
                answerSubmissions.add(answerSubmission);
            }));

            quizSubmission.setAnswerSubmissions(answerSubmissions);

            quizUtil.gradeQuiz(quiz, quizSubmission, person);

            return ResponseEntity.ok(
                    quizSubmissionAssembler.modelToDto(quizSubmissionService.save(quizSubmission))
            );
        } catch (ServiceException se) {
            throw new BadRequestException("Could not POST quiz submission!", se);
        }
    }
}
