package com.bbte.styoudent.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class DiscussionDto {
    private Long id;
    @NotEmpty
    @Size(max = 255)
    private String name;
    @NotEmpty
    @Size(max = 16384)
    private String content;
    @NotNull
    private LocalDateTime date;
    private List<@NotNull @Size(
            max = 255
    ) String> comments;
}
