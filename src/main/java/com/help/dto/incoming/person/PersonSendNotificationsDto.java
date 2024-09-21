package com.help.dto.incoming.person;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PersonSendNotificationsDto {
    @NotNull
    private Boolean sendNotifications;
}
