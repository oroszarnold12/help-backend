package com.help.dto.incoming.person;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ParticipationCreationDto {
    @NotNull
    private Long courseId;
    @NotNull
    private Boolean showOnDashboard;
}
