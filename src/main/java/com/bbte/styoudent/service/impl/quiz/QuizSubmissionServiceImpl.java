package com.bbte.styoudent.service.impl.quiz;

import com.bbte.styoudent.model.quiz.QuizSubmission;
import com.bbte.styoudent.repository.quiz.QuizSubmissionRepository;
import com.bbte.styoudent.service.quiz.QuizSubmissionService;
import com.bbte.styoudent.service.ServiceException;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuizSubmissionServiceImpl implements QuizSubmissionService {
    private final QuizSubmissionRepository quizSubmissionRepository;

    public QuizSubmissionServiceImpl(QuizSubmissionRepository quizSubmissionRepository) {
        this.quizSubmissionRepository = quizSubmissionRepository;
    }

    @Override
    public QuizSubmission save(QuizSubmission quizSubmission) {
        try {
            return quizSubmissionRepository.save(quizSubmission);
        } catch (DataAccessException de) {
            throw new ServiceException("Quiz submission insertion failed!", de);
        }
    }

    @Override
    public boolean checkIfExistsByQuizIdAndSubmitterId(Long quizId, Long submitterId) {
        try {
            return quizSubmissionRepository.existsByQuizIdAndSubmitterId(quizId, submitterId);
        } catch (DataAccessException de) {
            throw new ServiceException("Quiz submission checking failed!", de);
        }
    }

    @Override
    public List<QuizSubmission> getAllByQuizIdAndSubmitterId(Long quizId, Long submitterId) {
        try {
            return quizSubmissionRepository.findByQuizIdAndSubmitterId(quizId, submitterId);
        } catch (DataAccessException de) {
            throw new ServiceException("Quiz submission selection failed!", de);
        }
    }
}
