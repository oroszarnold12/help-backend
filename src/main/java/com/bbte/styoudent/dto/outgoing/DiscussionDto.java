package com.bbte.styoudent.dto.outgoing;

import com.bbte.styoudent.dto.outgoing.PersonDto;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class DiscussionDto {
    private Long id;
    private String name;
    private String content;
    private LocalDateTime date;
    private List<String> comments;
    private PersonDto creator;
}
