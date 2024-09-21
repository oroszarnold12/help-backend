package com.help.dto.outgoing.course;

import com.help.dto.outgoing.announcement.ThinAnnouncementDto;
import com.help.dto.outgoing.assignment.ThinAssignmentDto;
import com.help.dto.outgoing.discussion.ThinDiscussionDto;
import com.help.dto.outgoing.person.PersonDto;
import com.help.dto.outgoing.quiz.ThinQuizDto;
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
