package com.bbte.styoudent.dto.outgoing;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class DiscussionDto {
    private Long id;
    private String name;
    private String content;
    private LocalDateTime date;
    private List<String> comments;
    private ThinPersonDto creator;
}
