package com.bbte.styoudent.api.assembler;

import com.bbte.styoudent.dto.outgoing.GradeDto;
import com.bbte.styoudent.model.Grade;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class GradeAssembler {
    private final ModelMapper modelMapper;

    public GradeAssembler(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public GradeDto modelToDto(Grade grade) {
        return this.modelMapper.map(grade, GradeDto.class);
    }
}
