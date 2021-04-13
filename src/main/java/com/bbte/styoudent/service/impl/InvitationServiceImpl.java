package com.bbte.styoudent.service.impl;

import com.bbte.styoudent.model.Course;
import com.bbte.styoudent.model.Invitation;
import com.bbte.styoudent.model.Person;
import com.bbte.styoudent.repository.InvitationRepository;
import com.bbte.styoudent.service.InvitationService;
import com.bbte.styoudent.service.ServiceException;
import org.springframework.dao.DataAccessException;
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
    public Invitation createInvitation(Course course, Person person) {
        try {
            Invitation invitation = new Invitation();
            invitation.setCourse(course);
            invitation.setPerson(person);
            return invitationRepository.save(invitation);
        } catch (DataAccessException de) {
            throw new ServiceException("Invitation creation failed!", de);
        }
    }

    @Override
    public List<Invitation> getAllByPerson(Person person) {
        try {
            return invitationRepository.findAllByPerson(person);
        } catch (DataAccessException de) {
            throw new ServiceException("Invitation selection failed!", de);
        }
    }

    @Override
    public boolean checkIfExistsByIdAndPerson(Long id, Person person) {
        try {
            return invitationRepository.existsByIdAndPerson(id, person);
        } catch (DataAccessException de) {
            throw new ServiceException("Exists check failed!", de);
        }
    }

    @Override
    public void deleteInvitation(Long id) {
        try {
            invitationRepository.deleteById(id);
        } catch (IllegalArgumentException ie) {
            throw new ServiceException("Invitation deletion with id: " + id + " failed!");
        }
    }

    @Override
    public Optional<Invitation> getInvitationById(Long id) {
        try {
            return invitationRepository.findById(id);
        } catch (IllegalArgumentException ie) {
            throw new ServiceException("Invitation selection with id: " + id + " failed!", ie);
        }
    }

    @Override
    public boolean checkIfExistsByPersonIdAndCourseId(Long personId, Long courseId) {
        try {
            return invitationRepository.existsByPersonIdAndCourseId(personId, courseId);
        } catch (DataAccessException dataAccessException) {
            throw new ServiceException("Invitation checking failed!", dataAccessException);
        }
    }
}
