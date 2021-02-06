package com.bbte.styoudent.dto.outgoing;

import com.bbte.styoudent.model.Role;
import lombok.Data;

@Data
public class PersonDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
}
