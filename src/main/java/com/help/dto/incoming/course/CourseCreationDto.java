package com.help.dto.incoming.course;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class CourseCreationDto {
    @NotEmpty()
    @Size(max = 255)
    private String name;
    @NotEmpty()
    @Size(max = 255)
    private String longName;
    @NotEmpty
    @Size(max = 65536)
    private String description;
}
