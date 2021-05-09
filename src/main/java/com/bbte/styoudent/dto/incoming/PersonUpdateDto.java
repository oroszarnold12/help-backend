package com.bbte.styoudent.dto.incoming;

import com.bbte.styoudent.model.Role;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
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
    @Size(max = 255)
    @Pattern(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "Email address is not valid!")
    private String email;
    @NotNull
    private Role role;
}
