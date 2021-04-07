package com.bbte.styoudent.dto.outgoing;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CourseFileDto {
    private Long id;
    private String fileName;
    private Long size;
    private LocalDateTime creationDate;
    private ThinPersonDto uploader;
}
