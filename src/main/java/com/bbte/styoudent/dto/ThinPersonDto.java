package com.bbte.styoudent.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class ThinPersonDto {
    @NotEmpty
    @Size(max = 255)
    private String firstName;
    @NotEmpty
    @Size(max = 255)
    private String lastName;
    @NotEmpty
    @Size(max = 255)
    private String email;
}
