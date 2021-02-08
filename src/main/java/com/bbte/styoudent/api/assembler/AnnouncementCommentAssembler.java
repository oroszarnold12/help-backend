package com.bbte.styoudent.api.assembler;

import com.bbte.styoudent.dto.incoming.AnnouncementCommentCreationDto;
import com.bbte.styoudent.dto.outgoing.AnnouncementCommentDto;
import com.bbte.styoudent.model.AnnouncementComment;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class AnnouncementCommentAssembler {
    private final ModelMapper modelMapper;

    public AnnouncementCommentAssembler(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public AnnouncementComment creationDtoToModel(AnnouncementCommentCreationDto announcementCommentCreationDto) {
        return modelMapper.map(announcementCommentCreationDto, AnnouncementComment.class);
    }

    public AnnouncementCommentDto modelToDto(AnnouncementComment announcementComment) {
        return this.modelMapper.map(announcementComment, AnnouncementCommentDto.class);
    }
}
