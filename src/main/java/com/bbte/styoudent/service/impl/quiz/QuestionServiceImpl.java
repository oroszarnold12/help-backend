package com.bbte.styoudent.service.impl.quiz;

import com.bbte.styoudent.model.quiz.Question;
import com.bbte.styoudent.repository.quiz.QuestionRepository;
import com.bbte.styoudent.service.quiz.QuestionService;
import com.bbte.styoudent.service.ServiceException;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepository questionRepository;

    public QuestionServiceImpl(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Override
    public Question save(Question question) {
        try {
            return questionRepository.save(question);
        } catch (DataAccessException de) {
            throw new ServiceException("Question insertion failed!", de);
        }
    }

    @Override
    public List<Question> getAllByQuizId(Long quizId) {
        try {
            return questionRepository.findAllByQuizId(quizId);
        } catch (DataAccessException de) {
            throw new ServiceException("Question selection failed!", de);
        }
    }

    @Override
    public Question getByQuizIdAndId(Long quizId, Long id) {
        return questionRepository.findByQuizIdAndId(quizId, id).orElseThrow(() ->
                new ServiceException("Question selection with id: " + id + " failed!"));
    }

    @Override
    public boolean checkIfExistsByQuizIdAndId(Long quizId, Long id) {
        try {
            return questionRepository.existsByQuizIdAndId(quizId, id);
        } catch (DataAccessException de) {
            throw new ServiceException("Question checking failed!", de);
        }
    }

    @Override
    public void delete(Long id) {
        try {
            questionRepository.deleteById(id);
        } catch (DataAccessException de) {
            throw new ServiceException("Question deletion failed!", de);
        }
    }
}
