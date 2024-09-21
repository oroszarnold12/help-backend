package com.help.api.assembler;

import com.help.dto.outgoing.person.ParticipationDto;
import com.help.model.person.Participation;
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
