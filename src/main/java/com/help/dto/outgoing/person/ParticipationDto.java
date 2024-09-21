package com.help.dto.outgoing.person;

import com.help.dto.outgoing.course.ThinCourseDto;
import lombok.Data;

@Data
public class ParticipationDto {
    private ThinCourseDto course;
    private Boolean showOnDashboard;
}
