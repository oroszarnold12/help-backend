package com.bbte.styoudent.api.assembler;

import com.bbte.styoudent.dto.outgoing.AssignmentSubmissionDto;
import com.bbte.styoudent.model.AssignmentSubmission;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class SubmissionAssembler {
    private final ModelMapper modelMapper;

    public SubmissionAssembler(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public AssignmentSubmissionDto modelToDto(AssignmentSubmission assignmentSubmission) {
        return this.modelMapper.map(assignmentSubmission, AssignmentSubmissionDto.class);
    }
}
