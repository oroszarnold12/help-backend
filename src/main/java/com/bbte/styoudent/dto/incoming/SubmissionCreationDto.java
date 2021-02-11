package com.bbte.styoudent.dto.incoming;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;

@Data
public class SubmissionCreationDto {
    @NotEmpty
    private MultipartFile file;
}
