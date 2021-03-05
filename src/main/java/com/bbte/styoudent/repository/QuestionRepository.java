package com.bbte.styoudent.repository;

import com.bbte.styoudent.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findAllByQuizId(Long quizId);

    Optional<Question> findByQuizIdAndId(Long quizId, Long id);

    boolean existsByQuizIdAndId(Long quizId, Long id);
}
