package com.bbte.styoudent.api.assembler;

import com.bbte.styoudent.dto.incoming.AssignmentGradeCommentCreationDto;
import com.bbte.styoudent.dto.outgoing.AssignmentGradeCommentDto;
import com.bbte.styoudent.model.AssignmentGradeComment;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class AssignmentGradeCommentAssembler {
    private final ModelMapper modelMapper;

    public AssignmentGradeCommentAssembler(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public AssignmentGradeComment creationDtoToModel(AssignmentGradeCommentCreationDto assignmentGradeCommentCreationDto) {
        return modelMapper.map(assignmentGradeCommentCreationDto, AssignmentGradeComment.class);
    }

    public AssignmentGradeCommentDto modelToDto(AssignmentGradeComment assignmentGradeComment) {
        return this.modelMapper.map(assignmentGradeComment, AssignmentGradeCommentDto.class);
    }
}
