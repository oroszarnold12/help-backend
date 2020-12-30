package com.bbte.styoudent.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class InvitationDto {
    private Long id;
    @NotNull
    private CourseDto course;
}
