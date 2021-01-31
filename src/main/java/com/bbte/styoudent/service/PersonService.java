package com.bbte.styoudent.service;

import com.bbte.styoudent.model.Person;

import java.util.List;

public interface PersonService {
    Person getPersonByEmail(String email) throws ServiceException;
    Person savePerson(Person person) throws ServiceException;
    Person registerNewPerson(Person person) throws ServiceException;
    List<Person> getAllPersons();
    boolean checkIfExistsByEmail(String email);
}
