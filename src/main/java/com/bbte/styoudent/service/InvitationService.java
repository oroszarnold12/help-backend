package com.bbte.styoudent.service;

import com.bbte.styoudent.model.Course;
import com.bbte.styoudent.model.Invitation;
import com.bbte.styoudent.model.Person;

import java.util.List;
import java.util.Optional;

public interface InvitationService {
    void createInvitation(Course course, Person person);

    List<Invitation> getAllByPerson(Person person);

    boolean checkIfExistsByIdAndPerson(Long id, Person person);

    void deleteInvitation(Long id);

    Optional<Invitation> getInvitationById(Long id);
}
