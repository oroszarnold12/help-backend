package com.bbte.styoudent.api.assembler;

import com.bbte.styoudent.dto.outgoing.DiscussionDto;
import com.bbte.styoudent.dto.incoming.DiscussionCreationDto;
import com.bbte.styoudent.model.Discussion;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class DiscussionAssembler {
    private final ModelMapper modelMapper;

    public DiscussionAssembler(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public DiscussionDto modelToDto(Discussion discussion) {
        return modelMapper.map(discussion, DiscussionDto.class);
    }

    public Discussion creationDtoToModel(DiscussionCreationDto discussionCreationDto) {
        return modelMapper.map(discussionCreationDto, Discussion.class);
    }
}
