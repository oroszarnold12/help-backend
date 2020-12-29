package com.bbte.styoudent.api.controller;

import com.bbte.styoudent.api.exception.BadRequestException;
import com.bbte.styoudent.model.Person;
import com.bbte.styoudent.model.Role;
import com.bbte.styoudent.security.util.AuthUtil;
import com.bbte.styoudent.service.PersonService;
import com.bbte.styoudent.service.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/role")
public class RoleController {
    private final PersonService personService;

    public RoleController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Role>> getCourses() {
        log.debug("GET /role");

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());

        try {
            return ResponseEntity.ok(Collections.singletonMap("role",
                    person.getRole()));
        } catch (ServiceException se) {
            throw new BadRequestException("Could not GET role", se);
        }
    }
}
