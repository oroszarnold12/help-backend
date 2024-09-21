package com.help.api.assembler;

import com.help.dto.incoming.announcement.AnnouncementCommentCreationDto;
import com.help.dto.outgoing.announcement.AnnouncementCommentDto;
import com.help.dto.outgoing.announcement.AnnouncementDto;
import com.help.dto.incoming.announcement.AnnouncementCreationDto;
import com.help.model.announcement.Announcement;
import com.help.model.announcement.AnnouncementComment;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class AnnouncementAssembler {
    private final ModelMapper modelMapper;

    public AnnouncementAssembler(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Announcement creationDtoToModel(AnnouncementCreationDto announcementCreationDto) {
        return modelMapper.map(announcementCreationDto, Announcement.class);
    }

    public AnnouncementDto modelToDto(Announcement announcement) {
        return modelMapper.map(announcement, AnnouncementDto.class);
    }

    public AnnouncementComment creationDtoToModel(AnnouncementCommentCreationDto announcementCommentCreationDto) {
        return modelMapper.map(announcementCommentCreationDto, AnnouncementComment.class);
    }

    public AnnouncementCommentDto modelToDto(AnnouncementComment announcementComment) {
        return this.modelMapper.map(announcementComment, AnnouncementCommentDto.class);
    }
}
