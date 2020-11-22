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
}
