package com.bbte.styoudent.dto;

import com.bbte.styoudent.model.Role;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class PersonDto {
    private Long id;
    @NotEmpty
    @Size(max = 255)
    private String firstName;
    @NotEmpty
    @Size(max = 255)
    private String lastName;
    @NotEmpty
    @Size(max = 255)
    private String email;
    @NotNull
    private Role role;
}
