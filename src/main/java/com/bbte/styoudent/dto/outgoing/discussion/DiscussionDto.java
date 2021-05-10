package com.bbte.styoudent.dto.outgoing.discussion;

import com.bbte.styoudent.dto.outgoing.person.PersonDto;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class DiscussionDto {
    private Long id;
    private String name;
    private String content;
    private LocalDateTime date;
    private PersonDto creator;
    private List<DiscussionCommentDto> comments;
}
