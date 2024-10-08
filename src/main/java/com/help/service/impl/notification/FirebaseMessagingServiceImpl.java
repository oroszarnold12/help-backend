package com.help.service.impl.notification;

import com.help.model.notification.Note;
import com.help.service.notification.FirebaseMessagingService;
import com.help.service.ServiceException;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.stereotype.Service;

@Service
public class FirebaseMessagingServiceImpl implements FirebaseMessagingService {
    private final FirebaseMessaging firebaseMessaging;

    public FirebaseMessagingServiceImpl(FirebaseMessaging firebaseMessaging) {
        this.firebaseMessaging = firebaseMessaging;
    }

    @Override
    public void sendNotification(Note note, String token) {
        Notification notification = Notification
                .builder()
                .setTitle(note.getTitle())
                .setBody(note.getBody())
                .build();

        Message message = Message.builder()
                .setToken(token)
                .setNotification(notification)
                .putAllData(note.getData())
                .build();

        try {
            firebaseMessaging.send(message);
        } catch (FirebaseMessagingException firebaseMessagingException) {
            throw new ServiceException("Notification sending failed!", firebaseMessagingException);
        }
    }
}
