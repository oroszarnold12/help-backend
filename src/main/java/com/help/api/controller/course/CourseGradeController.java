package com.help.api.controller.course;

import com.help.api.assembler.GradeAssembler;
import com.help.api.exception.InternalServerException;
import com.help.dto.outgoing.assignment.AssignmentGradeDto;
import com.help.dto.outgoing.quiz.QuizGradeDto;
import com.help.model.person.Person;
import com.help.security.util.AuthUtil;
import com.help.service.ServiceException;
import com.help.service.assignment.AssignmentService;
import com.help.service.person.PersonService;
import com.help.service.quiz.QuizService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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

            Map<String, List<?>> grades = new ConcurrentHashMap<>(
                    Collections.singletonMap("assignmentGrades", assignmentGrades)
            );
            grades.put("quizGrades", quizGrades);

            return ResponseEntity.ok(grades);
        } catch (ServiceException se) {
            throw new InternalServerException("Could not GET grades!", se);
        }
    }
}
