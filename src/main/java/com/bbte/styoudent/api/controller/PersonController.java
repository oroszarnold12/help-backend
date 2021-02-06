package com.bbte.styoudent.api.controller;

import com.bbte.styoudent.api.assembler.PersonAssembler;
import com.bbte.styoudent.api.exception.BadRequestException;
import com.bbte.styoudent.api.exception.InternalServerException;
import com.bbte.styoudent.dto.outgoing.PersonDto;
import com.bbte.styoudent.dto.incoming.PersonUpdateDto;
import com.bbte.styoudent.dto.outgoing.ApiResponseMessage;
import com.bbte.styoudent.model.Person;
import com.bbte.styoudent.model.Role;
import com.bbte.styoudent.security.util.AuthUtil;
import com.bbte.styoudent.service.PersonService;
import com.bbte.styoudent.service.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/persons")
public class PersonController {
    private final PersonService personService;
    private final PersonAssembler personAssembler;

    public PersonController(PersonService personService,
                            PersonAssembler personAssembler) {
        this.personService = personService;
        this.personAssembler = personAssembler;
    }

    @GetMapping
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    ResponseEntity<Map<String, List<?>>> getPersons() {
        log.debug("GET /persons");

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());

        try {
            if (person.getRole() == Role.ROLE_TEACHER) {
                return ResponseEntity.ok(
                        Collections.singletonMap("persons", personService.getAllPersons()
                                .stream().map(personAssembler::modelToThinDto)
                                .collect(Collectors.toList())));
            } else {
                return ResponseEntity.ok(
                        Collections.singletonMap("persons", personService.getAllPersons()
                                .stream().map(personAssembler::modelToDto)
                                .collect(Collectors.toList())));
            }
        } catch (ServiceException se) {
            throw new InternalServerException("Could not GET persons!", se);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<PersonDto> updatePerson(@PathVariable(name = "id") Long id,
                                           @RequestBody @Valid PersonUpdateDto personUpdateDto) {
        log.debug("PUT /persons/{}", id);

        try {
            Person person = personService.getPersonById(id);
            Person incomingPerson = personAssembler.updateDtoToModel(personUpdateDto);

            person.setRole(incomingPerson.getRole());
            personService.savePerson(person);

            return ResponseEntity.ok(personAssembler.modelToDto(person));
        } catch (ServiceException se) {
            throw new BadRequestException("Could not put person with id: " + id + "!", se);
        }
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<ApiResponseMessage> deletePerson(@PathVariable(name = "id") Long id) {
        log.debug("DELETE /persons/{}", id);

        try {
            if (personService.getPersonById(id).getRole() == Role.ROLE_ADMIN) {
                throw new BadRequestException("Cannot delete person with admin role!");
            }

            personService.delete(id);

            return ResponseEntity.ok().body(new ApiResponseMessage("Person deletion with id " + id + " successful!"));
        } catch (ServiceException se) {
            throw new BadRequestException("Could not DELETE person with id " + id + "!", se);
        }
    }
}
