package com.bbte.styoudent.dto.outgoing;

import lombok.Data;

import java.util.List;

@Data
public class CourseDto {
    private Long id;
    private String name;
    private String longName;
    private String description;
    private List<ThinAssignmentDto> assignments;
    private List<ThinAnnouncementDto> announcements;
    private List<ThinDiscussionDto> discussions;
    private List<ThinQuizDto> quizzes;
    private List<CourseFileDto> files;
    private PersonDto teacher;
}
