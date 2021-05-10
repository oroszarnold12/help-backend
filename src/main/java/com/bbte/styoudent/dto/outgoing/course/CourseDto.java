package com.bbte.styoudent.dto.outgoing.course;

import com.bbte.styoudent.dto.outgoing.announcement.ThinAnnouncementDto;
import com.bbte.styoudent.dto.outgoing.assignment.ThinAssignmentDto;
import com.bbte.styoudent.dto.outgoing.discussion.ThinDiscussionDto;
import com.bbte.styoudent.dto.outgoing.person.PersonDto;
import com.bbte.styoudent.dto.outgoing.quiz.ThinQuizDto;
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
