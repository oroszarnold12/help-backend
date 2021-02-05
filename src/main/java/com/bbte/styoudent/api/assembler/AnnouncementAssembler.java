package com.bbte.styoudent.api.assembler;

import com.bbte.styoudent.dto.AnnouncementDto;
import com.bbte.styoudent.dto.incoming.AnnouncementCreationDto;
import com.bbte.styoudent.model.Announcement;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class AnnouncementAssembler {
    private final ModelMapper modelMapper;

    public AnnouncementAssembler(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Announcement dtoToModel(AnnouncementDto announcementDto) {
        return modelMapper.map(announcementDto, Announcement.class);
    }

    public Announcement creationDtoToModel(AnnouncementCreationDto announcementCreationDto) {
        return modelMapper.map(announcementCreationDto, Announcement.class);
    }

    public AnnouncementDto modelToDto(Announcement announcement) {
        return modelMapper.map(announcement, AnnouncementDto.class);
    }
}
