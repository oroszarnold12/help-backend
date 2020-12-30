package com.bbte.styoudent.service;

import com.bbte.styoudent.model.Course;
import com.bbte.styoudent.model.Participation;
import com.bbte.styoudent.model.Person;

import java.util.List;

public interface ParticipationService {
    void createInitialParticipation(Course course, Person person);
    void deleteParticipationsByCourse(Course course) throws ServiceException;
    boolean checkIfParticipates(Course course, Person person);
}
