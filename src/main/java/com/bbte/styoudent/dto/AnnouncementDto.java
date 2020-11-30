package com.bbte.styoudent.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AnnouncementDto {
    private Long id;
    private String name;
    private LocalDateTime date;
    private String content;
}
