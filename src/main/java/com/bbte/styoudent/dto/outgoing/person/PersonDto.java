package com.bbte.styoudent.dto.outgoing.person;

import com.bbte.styoudent.model.person.Role;
import lombok.Data;

@Data
public class PersonDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String personGroup;
    private Boolean sendNotifications;
    private Role role;
}
