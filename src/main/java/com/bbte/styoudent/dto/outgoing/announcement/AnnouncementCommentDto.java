package com.bbte.styoudent.dto.outgoing.announcement;

import com.bbte.styoudent.dto.outgoing.person.PersonDto;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AnnouncementCommentDto {
    private Long id;
    private String content;
    private LocalDateTime date;
    private PersonDto commenter;
}
