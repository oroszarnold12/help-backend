package com.help.dto.incoming.person;

import com.help.model.person.Role;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class PersonUpdateDto {
    @NotEmpty
    @Size(max = 255)
    private String firstName;
    @NotEmpty
    @Size(max = 255)
    private String lastName;
    @NotEmpty
    @Size(max = 255)
    private String personGroup;
    @NotNull
    private Role role;
}
