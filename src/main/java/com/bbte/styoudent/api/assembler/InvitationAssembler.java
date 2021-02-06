package com.bbte.styoudent.api.assembler;

import com.bbte.styoudent.dto.outgoing.InvitationDto;
import com.bbte.styoudent.model.Invitation;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class InvitationAssembler {
    private final ModelMapper modelMapper;

    public InvitationAssembler(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public InvitationDto modelToDto(Invitation invitation) {
        return modelMapper.map(invitation, InvitationDto.class);
    }
}
