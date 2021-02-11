package com.bbte.styoudent.api.assembler;

import com.bbte.styoudent.dto.outgoing.SubmissionDto;
import com.bbte.styoudent.model.Submission;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class SubmissionAssembler {
    private final ModelMapper modelMapper;

    public SubmissionAssembler(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public SubmissionDto modelToDto(Submission submission) {
        return this.modelMapper.map(submission, SubmissionDto.class);
    }
}
