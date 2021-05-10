package com.bbte.styoudent.service.notification;

import com.bbte.styoudent.model.notification.Note;

public interface FirebaseMessagingService {
    void sendNotification(Note note, String token);
}
