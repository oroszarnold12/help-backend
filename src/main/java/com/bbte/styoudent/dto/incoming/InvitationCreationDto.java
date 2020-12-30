package com.bbte.styoudent.dto.incoming;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class InvitationCreationDto {
    @NotEmpty
    private String[] emails;
    @NotNull
    private Long courseId;
}
