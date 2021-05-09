package com.bbte.styoudent.api.controller;

import com.bbte.styoudent.api.exception.InternalServerException;
import com.bbte.styoudent.service.PersonService;
import com.bbte.styoudent.service.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@CrossOrigin
@RestController
@Slf4j
@RequestMapping("/groups")
public class GroupController {
    private final PersonService personService;

    public GroupController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN') or hasRole('STUDENT')")
    ResponseEntity<Map<String, List<?>>> getPersons() {
        log.debug("GET /groups");

        try {
            Set<String> personGroups = new HashSet<>();

            personService.getAllPersons().forEach(person -> {
                if (person.getPersonGroup() != null) {
                    personGroups.add(person.getPersonGroup());
                }
            });

            return ResponseEntity.ok(
                    Collections.singletonMap("personGroups", new ArrayList<>(personGroups))
            );
        } catch (ServiceException se) {
            throw new InternalServerException("Could not GET persons!", se);
        }
    }
}
