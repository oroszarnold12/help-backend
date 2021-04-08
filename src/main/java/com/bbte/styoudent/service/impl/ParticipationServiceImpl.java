package com.bbte.styoudent.service.impl;

import com.bbte.styoudent.model.Course;
import com.bbte.styoudent.model.Participation;
import com.bbte.styoudent.model.Person;
import com.bbte.styoudent.repository.ParticipationRepository;
import com.bbte.styoudent.service.ParticipationService;
import com.bbte.styoudent.service.ServiceException;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

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
        participation.setShowOnDashboard(true);
        participationRepository.save(participation);
    }

    @Override
    public void deleteParticipationsByCourse(Course course) throws ServiceException {
        try {
            participationRepository.deleteParticipationsByCourse(course);
        } catch (DataAccessException de) {
            throw new ServiceException("Participation deletion by course with id " + course.getId() + " failed!" + de);
        }
    }

    @Override
    public boolean checkIfParticipates(Long courseId, Person person) {
        try {
            return participationRepository.existsByCourseIdAndPerson(courseId, person);
        } catch (DataAccessException de) {
            throw new ServiceException("Participation checking failed!", de);
        }
    }

    @Override
    public List<Participation> getAllByPerson(Person person) {
        try {
            return participationRepository.findAllByPerson(person);
        } catch (DataAccessException de) {
            throw new ServiceException("Participation selection failed!", de);
        }
    }

    @Override
    public Participation getByCourseIdAndPerson(Long courseId, Person person) {
        return participationRepository.findByCourseIdAndPerson(courseId, person).orElseThrow(() ->
                new ServiceException("Participation not found!")
        );
    }

    @Override
    public Participation save(Participation participation) {
        try {
            return participationRepository.save(participation);
        } catch (DataAccessException de) {
            throw new ServiceException("Participation insertion failed!", de);
        }
    }

    @Transactional
    @Override
    public void deleteByParticipantIdAndCourseId(Long participantId, Long courseId) {
        try {
            participationRepository.deleteByPersonIdAndCourseId(participantId, courseId);
        } catch (DataAccessException dataAccessException) {
            dataAccessException.printStackTrace();
            throw new ServiceException("Participation deletion failed!");
        }
    }

}
