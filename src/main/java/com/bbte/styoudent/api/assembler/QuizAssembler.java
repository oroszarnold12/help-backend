package com.bbte.styoudent.api.assembler;

import com.bbte.styoudent.dto.incoming.QuizCreationDto;
import com.bbte.styoudent.dto.outgoing.QuizDto;
import com.bbte.styoudent.model.Quiz;
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
}
