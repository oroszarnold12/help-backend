package com.bbte.styoudent.dto.outgoing.person;

import com.bbte.styoudent.dto.outgoing.course.ThinCourseDto;
import lombok.Data;

@Data
public class ParticipationDto {
    private ThinCourseDto course;
    private Boolean showOnDashboard;
}
