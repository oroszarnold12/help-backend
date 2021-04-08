package com.bbte.styoudent.service;

import com.bbte.styoudent.model.Course;
import com.bbte.styoudent.model.Participation;
import com.bbte.styoudent.model.Person;

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
