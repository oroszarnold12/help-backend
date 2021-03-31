package com.bbte.styoudent.dto.outgoing;

import lombok.Data;

@Data
public class ParticipationDto {
    private ThinCourseDto course;
    private Boolean showOnDashboard;
}
