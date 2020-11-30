package com.bbte.styoudent.api.assembler;

import com.bbte.styoudent.dto.DiscussionDto;
import com.bbte.styoudent.model.Discussion;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class DiscussionAssembler {
    private final ModelMapper modelMapper;

    public DiscussionAssembler(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Discussion dtoToModel(DiscussionDto discussionDto) {
        return modelMapper.map(discussionDto, Discussion.class);
    }

    public DiscussionDto modelToDto(Discussion discussion) {
        return modelMapper.map(discussion, DiscussionDto.class);
    }
}
