package com.bbte.styoudent.api.controller;

import com.bbte.styoudent.api.assembler.ThinPersonAssembler;
import com.bbte.styoudent.service.PersonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/persons")
public class PersonController {
    private final PersonService personService;
    private final ThinPersonAssembler thinPersonAssembler;

    public PersonController(PersonService personService, ThinPersonAssembler thinPersonAssembler) {
        this.personService = personService;
        this.thinPersonAssembler = thinPersonAssembler;
    }

    @GetMapping
    @PreAuthorize("hasRole('TEACHER')")
    ResponseEntity<Map<String, List<?>>> getPersons() {
        return ResponseEntity.ok(
                Collections.singletonMap("persons", personService.getAllPersons()
                        .stream().map(thinPersonAssembler::modelToDto)
                        .collect(Collectors.toList()))
        );
    }
}
