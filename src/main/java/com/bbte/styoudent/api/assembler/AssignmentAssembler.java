package com.bbte.styoudent.api.assembler;

import com.bbte.styoudent.dto.outgoing.AssignmentDto;
import com.bbte.styoudent.dto.incoming.AssignmentCreationDto;
import com.bbte.styoudent.model.Assignment;
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
}
