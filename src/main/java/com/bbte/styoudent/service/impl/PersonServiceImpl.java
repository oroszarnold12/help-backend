package com.bbte.styoudent.service.impl;

import com.bbte.styoudent.model.Person;
import com.bbte.styoudent.repository.PersonRepository;
import com.bbte.styoudent.service.PersonService;
import com.bbte.styoudent.service.ServiceException;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonServiceImpl implements PersonService {
    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;

    public PersonServiceImpl(PersonRepository personRepository, PasswordEncoder passwordEncoder) {
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Person getPersonByEmail(String email) throws ServiceException {
        return personRepository.findPersonByEmail(email).orElseThrow(
                () -> new ServiceException("Person not found!")
        );
    }

    @Override
    public Person savePerson(Person person) throws ServiceException {
        try {
            return personRepository.save(person);
        } catch (DataAccessException de) {
            throw new ServiceException("Person insertion failed!", de);
        }
    }

    @Override
    public Person registerNewPerson(Person person) throws ServiceException {
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        return savePerson(person);
    }

    @Override
    public List<Person> getAllPersons() {
        return personRepository.findAll();
    }

    @Override
    public boolean checkIfExistsByEmail(String email) {
        return personRepository.existsByEmail(email);
    }
}
