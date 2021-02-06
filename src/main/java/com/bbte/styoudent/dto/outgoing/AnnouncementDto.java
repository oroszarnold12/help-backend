package com.bbte.styoudent.dto.outgoing;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AnnouncementDto {
    private Long id;
    private String name;
    private LocalDateTime date;
    private String content;
    private PersonDto creator;
}
