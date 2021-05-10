package com.bbte.styoudent.repository.quiz;

import com.bbte.styoudent.model.person.Person;
import com.bbte.styoudent.model.quiz.QuizGrade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizGradeRepository extends JpaRepository<QuizGrade, Long> {
    List<QuizGrade> findByQuizIdAndSubmitter(Long quizId, Person submitter);

    List<QuizGrade> findByQuizId(Long quizId);

    boolean existsByQuizIdAndSubmitter(Long quizId, Person submitter);
}
