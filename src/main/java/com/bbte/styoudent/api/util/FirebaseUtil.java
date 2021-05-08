package com.bbte.styoudent.api.util;

import com.bbte.styoudent.api.exception.InternalServerException;
import com.bbte.styoudent.model.Note;
import com.bbte.styoudent.model.Person;
import com.bbte.styoudent.service.FirebaseMessagingService;
import com.bbte.styoudent.service.ServiceException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class FirebaseUtil {
    private final FirebaseMessagingService firebaseMessagingService;

    public FirebaseUtil(FirebaseMessagingService firebaseMessagingService) {
        this.firebaseMessagingService = firebaseMessagingService;
    }

    public void sendNotification(Note note, Person recipient, String entity) {
        if (recipient.getNotificationToken() != null) {
            try {
                firebaseMessagingService.sendNotification(note, recipient.getNotificationToken());
            } catch (ServiceException serviceException) {
                throw new InternalServerException(
                        "Could not send " + entity + " notification!", serviceException
                );
            }
        }
    }

    public void sendMultipleNotification(Note note, List<Person> recipients, String entity) {
        List<String> failedFor = new ArrayList<>();

        recipients.forEach(recipient -> {
            try {
                sendNotification(note, recipient, entity);
            } catch (InternalServerException internalServerException) {
                failedFor.add(recipient.getFirstName() + recipient.getLastName());
            }
        });

        if (!failedFor.isEmpty()) {
            throw new InternalServerException(
                    "Could not send " + entity + " notification for people: " + String.join(", ", failedFor)
            );
        }
    }
}
