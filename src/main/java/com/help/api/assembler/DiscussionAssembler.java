package com.help.api.assembler;

import com.help.dto.incoming.discussion.DiscussionCommentCreationDto;
import com.help.dto.outgoing.discussion.DiscussionCommentDto;
import com.help.dto.outgoing.discussion.DiscussionDto;
import com.help.dto.incoming.discussion.DiscussionCreationDto;
import com.help.model.discussion.Discussion;
import com.help.model.discussion.DiscussionComment;
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
