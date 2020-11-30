package com.bbte.styoudent.dto;

import lombok.Data;

import java.util.List;

@Data
public class CourseDto {
    private Long id;
    private String name;
    private String description;
    private List<AssignmentDto> assignments;
    private List<AnnouncementDto> announcements;
    private List<DiscussionDto> discussions;
}
