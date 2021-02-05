package com.bbte.styoudent.api.assembler;

import com.bbte.styoudent.dto.CourseDto;
import com.bbte.styoudent.dto.incoming.CourseCreationDto;
import com.bbte.styoudent.dto.incoming.CourseUpdateDto;
import com.bbte.styoudent.model.Course;
import com.bbte.styoudent.model.Person;
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

    public Course courseCreationDtoToModel(CourseCreationDto courseCreationDto, Person teacher) {
        Course course = modelMapper.map(courseCreationDto, Course.class);
        course.setTeacher(teacher);
        teacher.getCourses().add(course);
        course.setDescription("Default description");
        return course;
    }

    public void updateCourseFromDto(CourseUpdateDto courseUpdateDto, Course course) {
        clearCourse(course);
        modelMapper.map(courseUpdateDto, course);
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
