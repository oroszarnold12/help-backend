package com.bbte.styoudent.api.util;

import com.bbte.styoudent.api.assembler.PersonAssembler;
import com.bbte.styoudent.api.exception.ConflictException;
import com.bbte.styoudent.api.exception.InternalServerException;
import com.bbte.styoudent.dto.incoming.person.PersonSignUpDto;
import com.bbte.styoudent.dto.outgoing.person.PersonDto;
import com.bbte.styoudent.model.notification.Note;
import com.bbte.styoudent.model.person.Person;
import com.bbte.styoudent.service.person.PersonService;
import com.bbte.styoudent.service.ServiceException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PersonUtil {
    private final FirebaseUtil firebaseUtil;
    private final PersonAssembler personAssembler;
    private final PersonService personService;

    public PersonUtil(FirebaseUtil firebaseUtil, PersonAssembler personAssembler, PersonService personService) {
        this.firebaseUtil = firebaseUtil;
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
