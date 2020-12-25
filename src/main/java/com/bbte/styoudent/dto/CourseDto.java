package com.bbte.styoudent.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class CourseDto {
    private Long id;
    @NotEmpty
    @Size(max = 255)
    private String name;
    @NotEmpty
    @Size(max = 255)
    private String longName;
    @NotEmpty
    @Size(max = 65536)
    private String description;
    @Valid
    private List<AssignmentDto> assignments;
    @Valid
    private List<AnnouncementDto> announcements;
    @Valid
    private List<DiscussionDto> discussions;
}
