package com.help.api.controller.notification;

import com.help.api.exception.InternalServerException;
import com.help.model.person.Person;
import com.help.security.util.AuthUtil;
import com.help.service.person.PersonService;
import com.help.service.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;

@CrossOrigin
@RestController
@Slf4j
@RequestMapping("/notificationToken")
public class NotificationTokenController {
    private final PersonService personService;

    public NotificationTokenController(PersonService personService) {
        this.personService = personService;
    }

    @PostMapping
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER')")
    public void saveNotificationToken(@RequestBody @NotEmpty String token) {
        log.debug("POST /notificationToken");

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());
        person.setNotificationToken(token);

        try {
            personService.savePerson(person);
        } catch (ServiceException serviceException) {
            throw new InternalServerException("Could not save notification token!", serviceException);
        }
    }

    @DeleteMapping
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER')")
    public void deleteNotificationToken() {
        log.debug("DELETE /notificationToken");

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());
        person.setNotificationToken(null);

        try {
            personService.savePerson(person);
        } catch (ServiceException serviceException) {
            throw new InternalServerException("Could not delete notification token!", serviceException);
        }
    }
}
