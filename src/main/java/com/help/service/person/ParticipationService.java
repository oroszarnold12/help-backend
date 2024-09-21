package com.help.service.person;

import com.help.model.course.Course;
import com.help.model.person.Participation;
import com.help.model.person.Person;

import java.util.List;

public interface ParticipationService {
    void createInitialParticipation(Course course, Person person);

    void deleteParticipationsByCourse(Course course);

    boolean checkIfParticipates(Long courseId, Person person);

    List<Participation> getAllByPerson(Person person);

    Participation getByCourseIdAndPerson(Long courseId, Person person);

    Participation save(Participation participation);

    void deleteByParticipantIdAndCourseId(Long participantId, Long courseId);
}
