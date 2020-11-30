package com.bbte.styoudent.api.assembler;

import com.bbte.styoudent.dto.AssignmentDto;
import com.bbte.styoudent.model.Assignment;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class AssignmentAssembler {
    private final ModelMapper modelMapper;

    public AssignmentAssembler(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Assignment dtoToModel(AssignmentDto assignmentDto) {
        return modelMapper.map(assignmentDto, Assignment.class);
    }

    public AssignmentDto modelToDto(Assignment assignment) {
        return modelMapper.map(assignment, AssignmentDto.class);
    }
}
