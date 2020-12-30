package com.bbte.styoudent.service.impl;

import com.bbte.styoudent.model.Course;
import com.bbte.styoudent.model.Participation;
import com.bbte.styoudent.model.Person;
import com.bbte.styoudent.repository.ParticipationRepository;
import com.bbte.styoudent.service.ParticipationService;
import com.bbte.styoudent.service.ServiceException;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
public class ParticipationServiceImpl implements ParticipationService {
    private final ParticipationRepository participationRepository;

    public ParticipationServiceImpl(ParticipationRepository participationRepository) {
        this.participationRepository = participationRepository;
    }

    @Override
    public void createInitialParticipation(Course course, Person person) {
        Participation participation = new Participation();
        participation.setCourse(course);
        participation.setPerson(person);
        participationRepository.save(participation);
    }

    @Override
    public void deleteParticipationsByCourse(Course course) throws ServiceException {
        try {
            participationRepository.deleteParticipationsByCourse(course);
        } catch (DataAccessException de) {
            throw new ServiceException("Participation deletion by course with id " + course.getId() + " failed. " +de);
        }
    }

    @Override
    public boolean checkIfParticipates(Course course, Person person) {
        return participationRepository.existsByCourseAndPerson(course, person);
    }
}
