package com.bbte.styoudent.dto.outgoing;

import lombok.Data;

@Data
public class ThinAnswerSubmissionDto {
    private ThinAnswerDto answer;
    private Boolean picked;
}
