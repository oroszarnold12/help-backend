package com.help.service.impl.person;

import com.help.model.course.Course;
import com.help.model.person.Person;
import com.help.repository.person.PersonRepository;
import com.help.service.person.PersonService;
import com.help.service.ServiceException;
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
    public Person getPersonByEmail(String email) {
        return personRepository.findPersonByEmail(email).orElseThrow(
                () -> new ServiceException("Person not found!")
        );
    }

    @Override
    public Person savePerson(Person person) {
        try {
            return personRepository.save(person);
        } catch (DataAccessException de) {
            throw new ServiceException("Person insertion failed!", de);
        }
    }

    @Override
    public Person registerNewPerson(Person person) {
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        return savePerson(person);
    }

    @Override
    public List<Person> getAllPersons() {
        try {
            return personRepository.findAll();
        } catch (DataAccessException de) {
            throw new ServiceException("Person selection failed!", de);
        }
    }

    @Override
    public boolean checkIfExistsByEmail(String email) {
        try {
            return personRepository.existsByEmail(email);
        } catch (DataAccessException de) {
            throw new ServiceException("Exists check failed!", de);
        }
    }

    @Override
    public void delete(Long id) {
        try {
            personRepository.deleteById(id);
        } catch (DataAccessException de) {
            throw new ServiceException("Person deletion with id: " + id + " failed!", de);
        }
    }

    @Override
    public Person getPersonById(Long id) {
        return personRepository.findById(id).orElseThrow(() ->
                new ServiceException("Person with id:" + id + " not found!"));
    }

    @Override
    public List<Person> getByCoursesContains(Course course) {
        try {
            return personRepository.findByCoursesContains(course);
        } catch (DataAccessException de) {
            throw new ServiceException("Person selection failed!", de);
        }
    }

    @Override
    public Person changePassword(Person person, String password) {
        person.setPassword(passwordEncoder.encode(password));

        try {
            return personRepository.save(person);
        } catch (DataAccessException de) {
            throw new ServiceException("Person updating failed!", de);
        }
    }
}
