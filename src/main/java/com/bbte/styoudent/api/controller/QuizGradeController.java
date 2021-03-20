package com.bbte.styoudent.api.controller;

import com.bbte.styoudent.api.assembler.GradeAssembler;
import com.bbte.styoudent.api.exception.InternalServerException;
import com.bbte.styoudent.api.util.ParticipationUtil;
import com.bbte.styoudent.api.util.QuizUtil;
import com.bbte.styoudent.dto.outgoing.QuizGradeDto;
import com.bbte.styoudent.model.Person;
import com.bbte.styoudent.model.Role;
import com.bbte.styoudent.security.util.AuthUtil;
import com.bbte.styoudent.service.*;
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
