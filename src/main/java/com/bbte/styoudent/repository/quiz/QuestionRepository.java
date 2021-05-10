package com.bbte.styoudent.repository.quiz;

import com.bbte.styoudent.model.quiz.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findAllByQuizId(Long quizId);

    Optional<Question> findByQuizIdAndId(Long quizId, Long id);

    boolean existsByQuizIdAndId(Long quizId, Long id);
}
