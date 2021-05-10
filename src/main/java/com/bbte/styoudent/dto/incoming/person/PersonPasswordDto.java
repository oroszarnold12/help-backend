package com.bbte.styoudent.dto.incoming.person;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class PersonPasswordDto {
    @NotEmpty
    @Pattern(regexp = ".*[0-9].*", message = "Password doesn't contain number!")
    @Pattern(regexp = ".*[a-z].*", message = "Password doesn't contain lowercase!")
    @Pattern(regexp = ".*[A-Z].*", message = "Password doesn't contain uppercase!")
    @Pattern(regexp = ".*[^A-Za-z0-9].*", message = "Password doesn't contain special character!")
    @Size(min = 8, max = 255)
    private String password;
}
