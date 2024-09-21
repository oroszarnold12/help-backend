package com.help.service.notification;

import com.help.model.notification.Note;

public interface FirebaseMessagingService {
    void sendNotification(Note note, String token);
}
