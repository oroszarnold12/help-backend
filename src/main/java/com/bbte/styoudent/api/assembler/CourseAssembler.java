package com.bbte.styoudent.api.assembler;

import com.bbte.styoudent.dto.CourseDto;
import com.bbte.styoudent.dto.incoming.CourseCreationDto;
import com.bbte.styoudent.model.Course;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class CourseAssembler {
    private final ModelMapper modelMapper;

    public CourseAssembler(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Course dtoToModel(CourseDto courseDto) {
        return modelMapper.map(courseDto, Course.class);
    }

    public CourseDto modelToDto(Course course) {
        return modelMapper.map(course, CourseDto.class);
    }

    public Course courseCreationDtoToModel(CourseCreationDto courseCreationDto) {
        return modelMapper.map(courseCreationDto, Course.class);
    }

    public void updateCourseFromDto(CourseDto courseDto, Course course) {
        clearCourse(course);
        modelMapper.map(courseDto, course);
        setEntityNestedObjectRelation(course);
    }

    private void clearCourse(Course course) {
        course.getAssignments().stream().forEach(assignment -> assignment.setCourse(null));
        course.getAssignments().clear();

        course.getAnnouncements().stream().forEach(announcement -> announcement.setCourse(null));
        course.getAnnouncements().clear();

        course.getDiscussions().stream().forEach(discussion -> discussion.setCourse(null));
        course.getDiscussions().clear();
    }

    private void setEntityNestedObjectRelation(Course course) {
        course.getAssignments().stream().forEach(assignment -> assignment.setCourse(course));

        course.getAnnouncements().stream().forEach(announcement -> announcement.setCourse(course));

        course.getDiscussions().stream().forEach(discussion -> discussion.setCourse(course));
    }
}
