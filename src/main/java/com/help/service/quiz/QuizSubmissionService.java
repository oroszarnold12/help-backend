package com.help.service.quiz;

import com.help.model.quiz.QuizSubmission;

import java.util.List;

public interface QuizSubmissionService {
    QuizSubmission save(QuizSubmission quizSubmission);

    boolean checkIfExistsByQuizIdAndSubmitterId(Long quizId, Long submitterId);

    List<QuizSubmission> getAllByQuizIdAndSubmitterId(Long quizId, Long submitterId);
}
