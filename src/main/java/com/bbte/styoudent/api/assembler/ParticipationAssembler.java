package com.bbte.styoudent.api.assembler;

import com.bbte.styoudent.dto.outgoing.ParticipationDto;
import com.bbte.styoudent.model.Participation;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ParticipationAssembler {
    private final ModelMapper modelMapper;

    public ParticipationAssembler(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public ParticipationDto modelToDto(Participation participation) {
        return this.modelMapper.map(participation, ParticipationDto.class);
    }
}
