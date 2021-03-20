package com.bbte.styoudent.service.impl;

import com.bbte.styoudent.model.Assignment;
import com.bbte.styoudent.model.Quiz;
import com.bbte.styoudent.repository.QuizRepository;
import com.bbte.styoudent.service.QuizService;
import com.bbte.styoudent.service.ServiceException;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuizServiceImpl implements QuizService {
    private final QuizRepository quizRepository;

    public QuizServiceImpl(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    @Override
    public Quiz save(Quiz quiz) {
        try {
            return quizRepository.save(quiz);
        } catch (DataAccessException de) {
            throw new ServiceException("Quiz insertion failed!", de);
        }
    }

    @Override
    public boolean checkIfExistsByCourseIdAndId(Long courseId, Long id) {
        try {
            return quizRepository.existsByCourseIdAndId(courseId, id);
        } catch (DataAccessException de) {
            throw new ServiceException("Quiz checking failed!", de);
        }
    }

    @Override
    public void delete(Long id) {
        try {
            quizRepository.deleteById(id);
        } catch (DataAccessException de) {
            throw new ServiceException("Quiz deletion failed!", de);
        }
    }

    @Override
    public Quiz getByCourseIdAndId(Long courseId, Long id) {
        return quizRepository.findByCourseIdAndId(courseId, id).orElseThrow(() ->
                new ServiceException("Quiz selection with id: " + id + " failed!"));
    }

    @Override
    public List<Quiz> getByCourseId(Long courseId) {
        try {
            return quizRepository.findByCourseId(courseId);
        } catch (DataAccessException de) {
            throw new ServiceException("Quiz selection failed!", de);
        }
    }
}
