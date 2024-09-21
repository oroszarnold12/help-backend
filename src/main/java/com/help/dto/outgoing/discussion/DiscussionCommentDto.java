package com.help.dto.outgoing.discussion;

import com.help.dto.outgoing.person.PersonDto;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DiscussionCommentDto {
    private Long id;
    private String content;
    private LocalDateTime date;
    private PersonDto commenter;
}
