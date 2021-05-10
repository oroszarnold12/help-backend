package com.bbte.styoudent.service.person;

import com.bbte.styoudent.model.course.Course;
import com.bbte.styoudent.model.person.Person;

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
