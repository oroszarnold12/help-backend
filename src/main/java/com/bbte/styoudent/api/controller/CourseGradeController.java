package com.bbte.styoudent.api.controller;

import com.bbte.styoudent.api.assembler.GradeAssembler;
import com.bbte.styoudent.api.exception.InternalServerException;
import com.bbte.styoudent.dto.outgoing.AssignmentGradeDto;
import com.bbte.styoudent.dto.outgoing.QuizGradeDto;
import com.bbte.styoudent.model.Person;
import com.bbte.styoudent.security.util.AuthUtil;
import com.bbte.styoudent.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/courses/{courseId}/grades")
public class CourseGradeController {
    private final PersonService personService;
    private final GradeAssembler gradeAssembler;
    private final AssignmentService assignmentService;
    private final QuizService quizService;

    public CourseGradeController(PersonService personService, GradeAssembler gradeAssembler,
                                 AssignmentService assignmentService, QuizService quizService) {
        this.personService = personService;
        this.gradeAssembler = gradeAssembler;
        this.assignmentService = assignmentService;
        this.quizService = quizService;
    }

    @GetMapping()
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER')")
    public ResponseEntity<Map<String, List<?>>> getGrades(
            @PathVariable(name = "courseId") Long courseId
    ) {
        log.debug("GET /courses/{}/grades/", courseId);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());

        try {
            List<AssignmentGradeDto> assignmentGrades = new ArrayList<>();

            assignmentService.getByCourseId(courseId).forEach(assignment -> assignment.getAssignmentGrades().forEach(
                    grade -> {
                        if (grade.getSubmitter().equals(person)) {
                            assignmentGrades.add(gradeAssembler.modelToDto(grade));
                        }
                    }));

            List<QuizGradeDto> quizGrades = new ArrayList<>();

            quizService.getByCourseId(courseId).forEach(quiz -> quiz.getQuizGrades().forEach(grade -> {
                if (grade.getSubmitter().equals(person)) {
                    quizGrades.add(gradeAssembler.modelToDto(grade));
                }
            }));

            Map<String, List<?>> grades = new HashMap<>(Collections.singletonMap("assignmentGrades", assignmentGrades));
            grades.put("quizGrades", quizGrades);

            return ResponseEntity.ok(grades);
        } catch (ServiceException se) {
            throw new InternalServerException("Could not GET grades!", se);
        }
    }
}
