package com.help.api.util;

import com.help.api.exception.ForbiddenException;
import com.help.api.exception.InternalServerException;
import com.help.model.person.Person;
import com.help.service.person.ParticipationService;
import com.help.service.ServiceException;
import org.springframework.stereotype.Component;

@Component
public class ParticipationUtil {
    private final ParticipationService participationService;

    public ParticipationUtil(ParticipationService participationService) {
        this.participationService = participationService;
    }

    public void checkIfParticipates(Long courseId, Person person) {
        try {
            if (!participationService.checkIfParticipates(courseId, person)) {
                throw new ForbiddenException("Access denied!");
            }
        } catch (ServiceException se) {
            throw new InternalServerException("Could not check participation!", se);
        }
    }
}
