package com.bbte.styoudent.dto.outgoing;

import com.bbte.styoudent.dto.outgoing.CourseDto;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class InvitationDto {
    private Long id;
    private CourseDto course;
}
