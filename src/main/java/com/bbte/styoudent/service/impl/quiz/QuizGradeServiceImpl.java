package com.bbte.styoudent.service.impl.quiz;

import com.bbte.styoudent.model.person.Person;
import com.bbte.styoudent.model.quiz.QuizGrade;
import com.bbte.styoudent.repository.quiz.QuizGradeRepository;
import com.bbte.styoudent.service.quiz.QuizGradeService;
import com.bbte.styoudent.service.ServiceException;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuizGradeServiceImpl implements QuizGradeService {
    private final QuizGradeRepository quizGradeRepository;

    public QuizGradeServiceImpl(QuizGradeRepository quizGradeRepository) {
        this.quizGradeRepository = quizGradeRepository;
    }

    @Override
    public List<QuizGrade> getByQuizIdAndBySubmitter(Long quizId, Person submitter) {
        try {
            return quizGradeRepository.findByQuizIdAndSubmitter(quizId, submitter);
        } catch (DataAccessException de) {
            throw new ServiceException("Quiz grade selection failed!", de);
        }
    }

    @Override
    public List<QuizGrade> getByQuizId(Long quizId) {
        try {
            return quizGradeRepository.findByQuizId(quizId);
        } catch (DataAccessException de) {
            throw new ServiceException("Quiz grade selection failed!", de);
        }
    }

    @Override
    public boolean checkIfExistsByQuizIdAndSubmitter(Long quizId, Person submitter) {
        try {
            return quizGradeRepository.existsByQuizIdAndSubmitter(quizId, submitter);
        } catch (DataAccessException de) {
            throw new ServiceException("Quiz grade checking failed!", de);
        }
    }

    @Override
    public QuizGrade save(QuizGrade quizGrade) {
        try {
            return quizGradeRepository.save(quizGrade);
        } catch (DataAccessException de) {
            throw new ServiceException("Quiz grade insertion failed!", de);
        }
    }
}
