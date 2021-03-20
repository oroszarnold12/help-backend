package com.bbte.styoudent.service;

import com.bbte.styoudent.model.Person;
import com.bbte.styoudent.model.QuizGrade;

import java.util.List;

public interface QuizGradeService {
    List<QuizGrade> getByQuizIdAndBySubmitter(Long quizId, Person submitter);

    List<QuizGrade> getByQuizId(Long quizId);

    boolean checkIfExistsByQuizIdAndSubmitter(Long quizId, Person submitter);

    QuizGrade save(QuizGrade quizGrade);
}
