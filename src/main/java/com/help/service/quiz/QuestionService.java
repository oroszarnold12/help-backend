package com.help.service.quiz;

import com.help.model.quiz.Question;

import java.util.List;

public interface QuestionService {
    Question save(Question question);

    List<Question> getAllByQuizId(Long quizId);

    Question getByQuizIdAndId(Long quizId, Long id);

    boolean checkIfExistsByQuizIdAndId(Long quizId, Long id);

    void delete(Long id);
}
