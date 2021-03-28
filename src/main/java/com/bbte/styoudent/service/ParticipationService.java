package com.bbte.styoudent.service;

import com.bbte.styoudent.model.Course;
import com.bbte.styoudent.model.Person;

public interface ParticipationService {
    void createInitialParticipation(Course course, Person person);

    void deleteParticipationsByCourse(Course course);

    boolean checkIfParticipates(Long courseId, Person person);
}
