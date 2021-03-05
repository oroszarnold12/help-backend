package com.bbte.styoudent.api.assembler;

import com.bbte.styoudent.dto.incoming.QuestionCreationDto;
import com.bbte.styoudent.dto.outgoing.QuestionDto;
import com.bbte.styoudent.model.Question;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class QuestionAssembler {
    private final ModelMapper modelMapper;

    public QuestionAssembler(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Question creationDtoToModel(QuestionCreationDto questionCreationDto) {
        return modelMapper.map(questionCreationDto, Question.class);
    }

    public QuestionDto modelToDto(Question question) {
        return modelMapper.map(question, QuestionDto.class);
    }
}
