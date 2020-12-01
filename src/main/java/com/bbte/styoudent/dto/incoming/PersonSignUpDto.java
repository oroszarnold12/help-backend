package com.bbte.styoudent.dto.incoming;

import com.bbte.styoudent.model.Role;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class PersonSignUpDto {
    @NotEmpty
    @Size(max = 256)
    private String firstName;

    @NotEmpty
    @Size(max = 256)
    private String lastName;

    @Pattern(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "Email address is not valid!")
    private String email;

    @NotEmpty
    @Pattern(regexp = ".*[0-9].*", message = "Password doesn't contain number!")
    @Pattern(regexp = ".*[a-z].*", message = "Password doesn't contain lowercase!")
    @Pattern(regexp = ".*[A-Z].*", message = "Password doesn't contain uppercase!")
    @Pattern(regexp = ".*[^A-Za-z0-9].*", message = "Password doesn't contain special character!")
    @Size(min = 8)
    private String password;

    @NotNull
    private Role role;
}
