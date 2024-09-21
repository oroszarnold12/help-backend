package com.help.api.assembler;

import com.help.dto.outgoing.assignment.AssignmentCommentDto;
import com.help.dto.outgoing.assignment.AssignmentDto;
import com.help.dto.incoming.assignment.AssignmentCreationDto;
import com.help.dto.outgoing.assignment.AssignmentSubmissionDto;
import com.help.model.assignment.Assignment;
import com.help.model.assignment.AssignmentComment;
import com.help.model.assignment.AssignmentSubmission;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class AssignmentAssembler {
    private final ModelMapper modelMapper;

    public AssignmentAssembler(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Assignment creationDtoToModel(AssignmentCreationDto assignmentCreationDto) {
        return modelMapper.map(assignmentCreationDto, Assignment.class);
    }

    public AssignmentDto modelToDto(Assignment assignment) {
        return modelMapper.map(assignment, AssignmentDto.class);
    }

    public AssignmentCommentDto modelToDto(AssignmentComment assignmentComment) {
        return this.modelMapper.map(assignmentComment, AssignmentCommentDto.class);
    }

    public AssignmentSubmissionDto modelToDto(AssignmentSubmission assignmentSubmission) {
        return this.modelMapper.map(assignmentSubmission, AssignmentSubmissionDto.class);
    }
}
