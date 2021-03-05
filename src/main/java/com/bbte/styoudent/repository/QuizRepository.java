package com.bbte.styoudent.repository;

import com.bbte.styoudent.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    boolean existsByCourseIdAndId(Long courseId, Long id);

    Optional<Quiz> findByCourseIdAndId(Long courseId, Long id);
}
