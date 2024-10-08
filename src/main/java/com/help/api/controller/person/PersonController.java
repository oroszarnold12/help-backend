package com.help.api.controller.person;

import com.help.api.assembler.PersonAssembler;
import com.help.api.exception.BadRequestException;
import com.help.api.exception.InternalServerException;
import com.help.api.util.PersonUtil;
import com.help.dto.incoming.person.PersonSignUpDto;
import com.help.dto.incoming.person.PersonUpdateDto;
import com.help.dto.outgoing.ApiResponseMessage;
import com.help.dto.outgoing.person.PersonDto;
import com.help.model.person.Person;
import com.help.model.person.Role;
import com.help.security.util.AuthUtil;
import com.help.service.person.PersonService;
import com.help.service.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@Slf4j
@RequestMapping("/persons")
public class PersonController {
    private final PersonService personService;
    private final PersonAssembler personAssembler;
    private final PersonUtil personUtil;

    public PersonController(PersonService personService,
                            PersonAssembler personAssembler, PersonUtil personUtil) {
        this.personService = personService;
        this.personAssembler = personAssembler;
        this.personUtil = personUtil;
    }

    @GetMapping
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN') or hasRole('STUDENT')")
    ResponseEntity<Map<String, List<?>>> getPersons() {
        log.debug("GET /persons");

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());

        try {
            if (person.getRole() == Role.ROLE_TEACHER || person.getRole() == Role.ROLE_STUDENT) {
                return ResponseEntity.ok(
                        Collections.singletonMap("persons", personService.getAllPersons()
                                .stream().filter(person1 -> !person1.getRole().equals(Role.ROLE_ADMIN))
                                .map(personAssembler::modelToDto)
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

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PersonDto>> signUpPerson(
            @RequestBody @Valid @Size(min = 1) List<PersonSignUpDto> personSignUpDtos
    ) {
        List<Person> persons = personUtil.getPersons(personSignUpDtos);

        return ResponseEntity.ok(
                personUtil.registerPersons(persons)
        );
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
            person.setPersonGroup(incomingPerson.getPersonGroup());
            person.setFirstName(incomingPerson.getFirstName());
            person.setLastName(incomingPerson.getLastName());
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
