package com.bbte.styoudent.api.controller.notification;

import com.bbte.styoudent.api.exception.InternalServerException;
import com.bbte.styoudent.model.person.Person;
import com.bbte.styoudent.security.util.AuthUtil;
import com.bbte.styoudent.service.person.PersonService;
import com.bbte.styoudent.service.ServiceException;
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
