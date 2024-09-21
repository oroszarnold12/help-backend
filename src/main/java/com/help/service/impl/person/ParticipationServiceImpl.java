package com.help.service.impl.person;

import com.help.model.course.Course;
import com.help.model.person.Participation;
import com.help.model.person.Person;
import com.help.repository.person.ParticipationRepository;
import com.help.service.person.ParticipationService;
import com.help.service.ServiceException;
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
    public void deleteParticipationsByCourse(Course course) {
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
            throw new ServiceException("Participation deletion failed!");
        }
    }

}
