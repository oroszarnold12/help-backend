package com.bbte.styoudent.repository;

import com.bbte.styoudent.model.QuizSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizSubmissionRepository extends JpaRepository<QuizSubmission, Long> {
    boolean existsByQuizIdAndSubmitterId(Long quizId, Long submitterId);

    List<QuizSubmission> findByQuizIdAndSubmitterId(Long quizId, Long sumbitterId);
}
