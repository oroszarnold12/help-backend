package com.bbte.styoudent.api.assembler;

import com.bbte.styoudent.dto.outgoing.GradeDto;
import com.bbte.styoudent.dto.outgoing.QuizGradeDto;
import com.bbte.styoudent.model.AssignmentGrade;
import com.bbte.styoudent.model.QuizGrade;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class GradeAssembler {
    private final ModelMapper modelMapper;

    public GradeAssembler(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public GradeDto modelToDto(AssignmentGrade assignmentGrade) {
        return this.modelMapper.map(assignmentGrade, GradeDto.class);
    }

    public QuizGradeDto modelToDto(QuizGrade quizGrade) {
        return this.modelMapper.map(quizGrade, QuizGradeDto.class);
    }
}
