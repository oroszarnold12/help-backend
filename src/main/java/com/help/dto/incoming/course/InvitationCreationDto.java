package com.help.dto.incoming.course;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class InvitationCreationDto {
    private List<String> emails;
    private List<String> personGroups;
    @NotNull(message = "Id of course not provided")
    private Long courseId;
}
