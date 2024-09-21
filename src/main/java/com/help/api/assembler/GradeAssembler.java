package com.help.api.assembler;

import com.help.dto.outgoing.assignment.AssignmentGradeDto;
import com.help.dto.outgoing.quiz.QuizGradeDto;
import com.help.model.assignment.AssignmentGrade;
import com.help.model.quiz.QuizGrade;
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
