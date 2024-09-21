package com.help.api.assembler;

import com.help.dto.incoming.quiz.QuestionCreationDto;
import com.help.dto.incoming.quiz.QuizCreationDto;
import com.help.dto.outgoing.quiz.QuestionDto;
import com.help.dto.outgoing.quiz.QuizDto;
import com.help.dto.outgoing.quiz.QuizSubmissionDto;
import com.help.dto.outgoing.quiz.ThinQuizSubmissionDto;
import com.help.model.quiz.Question;
import com.help.model.quiz.Quiz;
import com.help.model.quiz.QuizSubmission;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class QuizAssembler {
    private final ModelMapper modelMapper;

    public QuizAssembler(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Quiz creationDtoToModel(QuizCreationDto quizCreationDto) {
        return modelMapper.map(quizCreationDto, Quiz.class);
    }

    public QuizDto modelToDto(Quiz quiz) {
        return modelMapper.map(quiz, QuizDto.class);
    }

    public Question creationDtoToModel(QuestionCreationDto questionCreationDto) {
        return modelMapper.map(questionCreationDto, Question.class);
    }

    public QuestionDto modelToDto(Question question) {
        return modelMapper.map(question, QuestionDto.class);
    }

    public QuizSubmissionDto modelToDto(QuizSubmission quizSubmission) {
        return modelMapper.map(quizSubmission, QuizSubmissionDto.class);
    }

    public ThinQuizSubmissionDto modelToThinDto(QuizSubmission quizSubmission) {
        return modelMapper.map(quizSubmission, ThinQuizSubmissionDto.class);
    }
}
