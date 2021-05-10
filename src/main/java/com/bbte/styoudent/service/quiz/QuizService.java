package com.bbte.styoudent.service.quiz;

import com.bbte.styoudent.model.quiz.Quiz;

import java.util.List;

public interface QuizService {
    Quiz save(Quiz quiz);

    boolean checkIfExistsByCourseIdAndId(Long courseId, Long id);

    void delete(Long id);

    Quiz getByCourseIdAndId(Long courseId, Long id);

    List<Quiz> getByCourseId(Long courseId);
}
