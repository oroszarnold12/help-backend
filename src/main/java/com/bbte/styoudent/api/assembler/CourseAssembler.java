package com.bbte.styoudent.api.assembler;

import com.bbte.styoudent.dto.incoming.CourseCreationDto;
import com.bbte.styoudent.dto.outgoing.CourseDto;
import com.bbte.styoudent.dto.outgoing.ThinCourseDto;
import com.bbte.styoudent.model.Course;
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
}
