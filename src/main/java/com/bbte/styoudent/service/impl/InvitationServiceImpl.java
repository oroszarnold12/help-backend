package com.bbte.styoudent.service.impl;

import com.bbte.styoudent.model.Course;
import com.bbte.styoudent.model.Invitation;
import com.bbte.styoudent.model.Person;
import com.bbte.styoudent.repository.InvitationRepository;
import com.bbte.styoudent.service.InvitationService;
import com.bbte.styoudent.service.ServiceException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InvitationServiceImpl implements InvitationService {
    private final InvitationRepository invitationRepository;

    public InvitationServiceImpl(InvitationRepository invitationRepository) {
        this.invitationRepository = invitationRepository;
    }

    @Override
    public void createInvitation(Course course, Person person) {
        Invitation invitation = new Invitation();
        invitation.setCourse(course);
        invitation.setPerson(person);
        invitationRepository.save(invitation);
    }

    @Override
    public List<Invitation> getAllByPerson(Person person) {
        return invitationRepository.findAllByPerson(person);
    }

    @Override
    public boolean checkIfExistsByIdAndPerson(Long id, Person person) {
        return invitationRepository.existsByIdAndPerson(id, person);
    }

    @Override
    public void deleteInvitation(Long id) {
        try {
            invitationRepository.deleteById(id);
        } catch (IllegalArgumentException ie) {
            throw new ServiceException("Invitation deletion with id: " + id + " failed");
        }
    }

    @Override
    public Optional<Invitation> getInvitationById(Long id) {
        try {
            return invitationRepository.findById(id);
        } catch (IllegalArgumentException ie) {
            throw  new ServiceException("Invitation selection with id: " + id + " failed", ie);
        }
    }
}
