package com.help.api.util;

import com.help.api.assembler.PersonAssembler;
import com.help.api.exception.ConflictException;
import com.help.api.exception.InternalServerException;
import com.help.dto.incoming.person.PersonSignUpDto;
import com.help.dto.outgoing.person.PersonDto;
import com.help.model.person.Person;
import com.help.service.ServiceException;
import com.help.service.person.PersonService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PersonUtil {
    private final PersonAssembler personAssembler;
    private final PersonService personService;

    public PersonUtil(PersonAssembler personAssembler, PersonService personService) {
        this.personAssembler = personAssembler;
        this.personService = personService;
    }

    public List<Person> getPersons(List<PersonSignUpDto> personSignUpDtos) {
        List<String> emailsAlreadyInUse = new ArrayList<>();
        List<Person> persons = new ArrayList<>();

        personSignUpDtos.forEach(personSignUpDto -> {
            Person person = personAssembler.signUpDtoToModel(personSignUpDto);
            person.setSendNotifications(true);

            try {
                if (personService.checkIfExistsByEmail(person.getEmail())) {
                    emailsAlreadyInUse.add(person.getEmail());
                } else {
                    persons.add(person);
                }
            } catch (ServiceException se) {
                throw new InternalServerException("Could not verify e-mail address!", se);
            }
        });

        if (!emailsAlreadyInUse.isEmpty()) {
            throw new ConflictException("E-mails already in use: " + String.join(",", emailsAlreadyInUse));
        }

        return persons;
    }

    public List<PersonDto> registerPersons(List<Person> persons) {
        List<String> failedFor = new ArrayList<>();
        List<Person> registeredPersons = new ArrayList<>();

        persons.forEach(person -> {
            try {
                personService.registerNewPerson(person);

                registeredPersons.add(person);
            } catch (ServiceException serviceException) {
                failedFor.add(person.getEmail());
            }
        });

        if (!failedFor.isEmpty()) {
            throw new InternalServerException("Registration failed for: " + String.join(",", failedFor));
        }

        return registeredPersons.stream().map(personAssembler::modelToDto).collect(Collectors.toList());
    }
}
