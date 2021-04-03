package com.bbte.styoudent.service;

import com.bbte.styoudent.model.Course;
import com.bbte.styoudent.model.Person;

import java.util.List;

public interface PersonService {
    Person getPersonByEmail(String email);

    Person savePerson(Person person);

    Person registerNewPerson(Person person);

    List<Person> getAllPersons();

    boolean checkIfExistsByEmail(String email);

    void delete(Long id);

    Person getPersonById(Long id);

    List<Person> getByCoursesContains(Course course);

    Person changePassword(Person person, String password);
}
