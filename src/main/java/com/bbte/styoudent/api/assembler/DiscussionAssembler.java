package com.bbte.styoudent.api.assembler;

import com.bbte.styoudent.dto.incoming.discussion.DiscussionCommentCreationDto;
import com.bbte.styoudent.dto.outgoing.discussion.DiscussionCommentDto;
import com.bbte.styoudent.dto.outgoing.discussion.DiscussionDto;
import com.bbte.styoudent.dto.incoming.discussion.DiscussionCreationDto;
import com.bbte.styoudent.model.discussion.Discussion;
import com.bbte.styoudent.model.discussion.DiscussionComment;
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

    public DiscussionComment creationDtoToModel(DiscussionCommentCreationDto discussionCommentCreationDto) {
        return modelMapper.map(discussionCommentCreationDto, DiscussionComment.class);
    }

    public DiscussionCommentDto modelToDto(DiscussionComment discussionComment) {
        return this.modelMapper.map(discussionComment, DiscussionCommentDto.class);
    }
}
