package com.help.dto.outgoing.discussion;

import com.help.dto.outgoing.person.PersonDto;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ThinDiscussionDto {
    private Long id;
    private String name;
    private LocalDateTime date;
    private PersonDto creator;
}
