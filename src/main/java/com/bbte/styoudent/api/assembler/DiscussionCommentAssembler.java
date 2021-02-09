package com.bbte.styoudent.api.assembler;

import com.bbte.styoudent.dto.incoming.DiscussionCommentCreationDto;
import com.bbte.styoudent.dto.outgoing.DiscussionCommentDto;
import com.bbte.styoudent.model.DiscussionComment;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class DiscussionCommentAssembler {
    private final ModelMapper modelMapper;

    public DiscussionCommentAssembler(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public DiscussionComment creationDtoToModel(DiscussionCommentCreationDto discussionCommentCreationDto) {
        return modelMapper.map(discussionCommentCreationDto, DiscussionComment.class);
    }

    public DiscussionCommentDto modelToDto(DiscussionComment discussionComment) {
        return this.modelMapper.map(discussionComment, DiscussionCommentDto.class);
    }
}
