package com.bbte.styoudent.service;

import com.bbte.styoudent.model.QuizSubmission;

import java.util.List;

public interface QuizSubmissionService {
    QuizSubmission save(QuizSubmission quizSubmission);

    boolean checkIfExistsByQuizIdAndSubmitterId(Long quizId, Long submitterId);

    List<QuizSubmission> getAllByQuizIdAndSubmitterId(Long quizId, Long submitterId);
}
