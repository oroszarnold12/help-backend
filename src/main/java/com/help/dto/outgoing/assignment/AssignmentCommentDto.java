package com.help.dto.outgoing.assignment;

import com.help.dto.outgoing.person.PersonDto;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AssignmentCommentDto {
    private Long id;
    private String content;
    private LocalDateTime date;
    private PersonDto commenter;
    private PersonDto recipient;
}
