package com.bbte.styoudent.api.util;

import com.bbte.styoudent.api.exception.ForbiddenException;
import com.bbte.styoudent.api.exception.InternalServerException;
import com.bbte.styoudent.model.Course;
import com.bbte.styoudent.model.Note;
import com.bbte.styoudent.model.Person;
import com.bbte.styoudent.service.FirebaseMessagingService;
import com.bbte.styoudent.service.ParticipationService;
import com.bbte.styoudent.service.ServiceException;
import org.springframework.stereotype.Component;

import java.util.HashMap;

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
