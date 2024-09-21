package com.help.service.impl.quiz;

import com.help.model.person.Person;
import com.help.model.quiz.QuizGrade;
import com.help.repository.quiz.QuizGradeRepository;
import com.help.service.quiz.QuizGradeService;
import com.help.service.ServiceException;
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
