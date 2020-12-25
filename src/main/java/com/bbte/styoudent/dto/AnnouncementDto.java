package com.bbte.styoudent.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
public class AnnouncementDto {
    private Long id;
    @NotEmpty
    @Size(max = 255)
    private String name;
    @NotNull
    private LocalDateTime date;
    @NotEmpty
    @Size(max = 16384)
    private String content;
}
