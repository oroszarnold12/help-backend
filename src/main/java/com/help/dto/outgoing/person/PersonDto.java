package com.help.dto.outgoing.person;

import com.help.model.person.Role;
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
