package com.bbte.styoudent.dto.incoming;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ParticipationCreationDto {
    @NotNull
    private Long courseId;
    @NotNull
    private Boolean showOnDashboard;
}
