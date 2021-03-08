package com.bbte.styoudent.api.assembler;

import com.bbte.styoudent.dto.outgoing.QuizSubmissionDto;
import com.bbte.styoudent.dto.outgoing.ThinQuizSubmissionDto;
import com.bbte.styoudent.model.QuizSubmission;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class QuizSubmissionAssembler {
    private final ModelMapper modelMapper;

    public QuizSubmissionAssembler(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public QuizSubmissionDto modelToDto(QuizSubmission quizSubmission) {
        return modelMapper.map(quizSubmission, QuizSubmissionDto.class);
    }

    public ThinQuizSubmissionDto modelToThinDto(QuizSubmission quizSubmission) {
        return modelMapper.map(quizSubmission, ThinQuizSubmissionDto.class);
    }
}
