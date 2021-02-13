package com.bbte.styoudent.dto.incoming;

import lombok.Data;
import lombok.NonNull;

@Data
public class GradeCreationDto {
    @NonNull
    private Double grade;
    @NonNull
    private Long personId;
}
