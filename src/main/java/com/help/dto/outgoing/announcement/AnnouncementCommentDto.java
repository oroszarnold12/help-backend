package com.help.dto.outgoing.announcement;

import com.help.dto.outgoing.person.PersonDto;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AnnouncementCommentDto {
    private Long id;
    private String content;
    private LocalDateTime date;
    private PersonDto commenter;
}
