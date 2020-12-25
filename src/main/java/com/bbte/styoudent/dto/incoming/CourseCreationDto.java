package com.bbte.styoudent.dto.incoming;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class CourseCreationDto {
    @NotEmpty(message = "Field name not provided")
    @Size(max = 255)
    private String name;

    @NotEmpty(message = "Field longName not provided")
    @Size(max = 255)
    private String longName;
}
