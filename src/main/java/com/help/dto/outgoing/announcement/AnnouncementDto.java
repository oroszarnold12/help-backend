package com.help.dto.outgoing.announcement;

import com.help.dto.outgoing.person.PersonDto;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AnnouncementDto {
    private Long id;
    private String name;
    private LocalDateTime date;
    private String content;
    private PersonDto creator;
    private List<AnnouncementCommentDto> comments;
}
