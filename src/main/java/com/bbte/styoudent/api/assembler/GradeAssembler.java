package com.bbte.styoudent.api.assembler;

import com.bbte.styoudent.dto.outgoing.AssignmentGradeDto;
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

    public AssignmentGradeDto modelToDto(AssignmentGrade assignmentGrade) {
        return this.modelMapper.map(assignmentGrade, AssignmentGradeDto.class);
    }

    public QuizGradeDto modelToDto(QuizGrade quizGrade) {
        return this.modelMapper.map(quizGrade, QuizGradeDto.class);
    }
}
