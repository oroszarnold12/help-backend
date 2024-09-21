package com.help.api.controller.quiz;

import com.help.api.assembler.GradeAssembler;
import com.help.api.exception.InternalServerException;
import com.help.api.util.ParticipationUtil;
import com.help.api.util.QuizUtil;
import com.help.dto.outgoing.quiz.QuizGradeDto;
import com.help.model.person.Person;
import com.help.model.person.Role;
import com.help.security.util.AuthUtil;
import com.help.service.ServiceException;
import com.help.service.person.PersonService;
import com.help.service.quiz.QuizGradeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/courses/{courseId}/quizzes/{quizId}/grades")
public class QuizGradeController {
    private final PersonService personService;
    private final QuizGradeService quizGradeService;
    private final GradeAssembler gradeAssembler;
    private final QuizUtil quizUtil;
    private final ParticipationUtil participationUtil;

    public QuizGradeController(PersonService personService, QuizGradeService quizGradeService,
                               GradeAssembler gradeAssembler, QuizUtil quizUtil, ParticipationUtil participationUtil) {
        this.personService = personService;
        this.quizGradeService = quizGradeService;
        this.gradeAssembler = gradeAssembler;
        this.quizUtil = quizUtil;
        this.participationUtil = participationUtil;
    }

    @GetMapping()
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER')")
    public ResponseEntity<List<QuizGradeDto>> getQuizGrades(
            @PathVariable(name = "courseId") Long courseId,
            @PathVariable(name = "quizId") Long quizId
    ) {
        log.debug("GET /courses/{}/quizzes/{}/grades", courseId, quizId);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());
        participationUtil.checkIfParticipates(courseId, person);
        quizUtil.checkIfHasThisQuiz(courseId, quizId);

        try {
            if (person.getRole().equals(Role.ROLE_STUDENT)) {
                return ResponseEntity.ok(quizGradeService.getByQuizIdAndBySubmitter(quizId, person).stream()
                        .map(gradeAssembler::modelToDto)
                        .collect(Collectors.toList()));
            } else {
                return ResponseEntity.ok(quizGradeService.getByQuizId(quizId).stream()
                        .map(gradeAssembler::modelToDto)
                        .collect(Collectors.toList()));
            }
        } catch (ServiceException se) {
            throw new InternalServerException("Could not GET quiz grades!", se);
        }
    }
}
