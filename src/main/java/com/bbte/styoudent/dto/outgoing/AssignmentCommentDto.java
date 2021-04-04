package com.bbte.styoudent.dto.outgoing;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AssignmentCommentDto {
    private Long id;
    private String content;
    private LocalDateTime date;
    private ThinPersonDto commenter;
    private ThinPersonDto recipient;
}
