package com.bbte.styoudent.api.assembler;

import com.bbte.styoudent.dto.incoming.course.CourseCreationDto;
import com.bbte.styoudent.dto.outgoing.course.CourseDto;
import com.bbte.styoudent.dto.outgoing.course.CourseFileDto;
import com.bbte.styoudent.dto.outgoing.course.ThinCourseDto;
import com.bbte.styoudent.model.course.Course;
import com.bbte.styoudent.model.course.CourseFile;
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
