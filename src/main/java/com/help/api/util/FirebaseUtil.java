package com.help.api.util;

import com.help.api.exception.InternalServerException;
import com.help.model.notification.Note;
import com.help.model.person.Person;
import com.help.service.notification.FirebaseMessagingService;
import com.help.service.ServiceException;
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
        if (recipient.getNotificationToken() != null && recipient.getSendNotifications()) {
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
