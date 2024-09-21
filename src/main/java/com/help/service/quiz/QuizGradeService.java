package com.help.service.quiz;

import com.help.model.person.Person;
import com.help.model.quiz.QuizGrade;

import java.util.List;

public interface QuizGradeService {
    List<QuizGrade> getByQuizIdAndBySubmitter(Long quizId, Person submitter);

    List<QuizGrade> getByQuizId(Long quizId);

    boolean checkIfExistsByQuizIdAndSubmitter(Long quizId, Person submitter);

    QuizGrade save(QuizGrade quizGrade);
}
