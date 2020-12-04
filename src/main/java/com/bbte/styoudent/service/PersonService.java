package com.bbte.styoudent.service;

import com.bbte.styoudent.model.Person;

public interface PersonService {
    Person getPersonByEmail(String email) throws ServiceException;
    Person savePerson(Person person) throws ServiceException;
    Person registerNewPerson(Person person) throws ServiceException;

}
