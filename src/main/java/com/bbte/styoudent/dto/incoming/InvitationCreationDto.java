package com.bbte.styoudent.dto.incoming;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class InvitationCreationDto {
    @NotEmpty(message = "Invitation does not contain any email")
    private String[] emails;
    @NotNull(message = "Id of course not provided")
    private Long courseId;
}
