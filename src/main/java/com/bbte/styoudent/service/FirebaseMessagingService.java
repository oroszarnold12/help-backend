package com.bbte.styoudent.service;

import com.bbte.styoudent.model.Note;

public interface FirebaseMessagingService {
    void sendNotification(Note note, String token);
}
