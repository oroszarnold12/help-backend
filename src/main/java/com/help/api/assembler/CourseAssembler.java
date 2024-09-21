package com.help.api.assembler;

import com.help.dto.incoming.course.CourseCreationDto;
import com.help.dto.outgoing.course.CourseDto;
import com.help.dto.outgoing.course.CourseFileDto;
import com.help.dto.outgoing.course.ThinCourseDto;
import com.help.model.course.Course;
import com.help.model.course.CourseFile;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class CourseAssembler {
    private final ModelMapper modelMapper;

    public CourseAssembler(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public CourseDto modelToDto(Course course) {
        return modelMapper.map(course, CourseDto.class);
    }

    public ThinCourseDto modelToThinDto(Course course) {
        return modelMapper.map(course, ThinCourseDto.class);
    }

    public Course creationDtoToModel(CourseCreationDto courseCreationDto) {
        return modelMapper.map(courseCreationDto, Course.class);
    }

    public CourseFileDto modelToDto(CourseFile courseFile) {
        return modelMapper.map(courseFile, CourseFileDto.class);
    }
}
