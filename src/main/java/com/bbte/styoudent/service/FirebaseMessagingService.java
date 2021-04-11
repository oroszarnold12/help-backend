package com.bbte.styoudent.service;

import com.bbte.styoudent.model.Note;

import java.util.Map;

public interface FirebaseMessagingService {
    void sendNotification(Note note, String token);
}
