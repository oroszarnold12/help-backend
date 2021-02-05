package com.bbte.styoudent.dto.incoming;

import com.bbte.styoudent.dto.AnnouncementDto;
import com.bbte.styoudent.dto.AssignmentDto;
import com.bbte.styoudent.dto.DiscussionDto;
import com.bbte.styoudent.dto.PersonDto;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class CourseUpdateDto {
    @NotEmpty
    @Size(max = 255)
    private String name;
    @NotEmpty
    @Size(max = 255)
    private String longName;
    @NotEmpty
    @Size(max = 65536)
    private String description;
    @Valid
    private List<AssignmentDto> assignments;
    @Valid
    private List<AnnouncementDto> announcements;
    @Valid
    private List<DiscussionDto> discussions;
    @NotNull
    private PersonDto teacher;
}
