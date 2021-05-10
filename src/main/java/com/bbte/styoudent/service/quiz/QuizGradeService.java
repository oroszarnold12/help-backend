package com.bbte.styoudent.service.quiz;

import com.bbte.styoudent.model.person.Person;
import com.bbte.styoudent.model.quiz.QuizGrade;

import java.util.List;

public interface QuizGradeService {
    List<QuizGrade> getByQuizIdAndBySubmitter(Long quizId, Person submitter);

    List<QuizGrade> getByQuizId(Long quizId);

    boolean checkIfExistsByQuizIdAndSubmitter(Long quizId, Person submitter);

    QuizGrade save(QuizGrade quizGrade);
}
