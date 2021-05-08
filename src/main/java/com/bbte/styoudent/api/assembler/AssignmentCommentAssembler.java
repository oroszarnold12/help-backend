package com.bbte.styoudent.api.assembler;

import com.bbte.styoudent.dto.outgoing.AssignmentCommentDto;
import com.bbte.styoudent.model.AssignmentComment;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class AssignmentCommentAssembler {
    private final ModelMapper modelMapper;

    public AssignmentCommentAssembler(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public AssignmentCommentDto modelToDto(AssignmentComment assignmentComment) {
        return this.modelMapper.map(assignmentComment, AssignmentCommentDto.class);
    }
}
