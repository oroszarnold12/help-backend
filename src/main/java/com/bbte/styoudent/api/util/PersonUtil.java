package com.bbte.styoudent.api.util;

import com.bbte.styoudent.model.Note;
import com.bbte.styoudent.model.Person;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class PersonUtil {
    private final FirebaseUtil firebaseUtil;

    public PersonUtil(FirebaseUtil firebaseUtil) {
        this.firebaseUtil = firebaseUtil;
    }

    public void createSingleNotificationOfRoleChange(Person person) {
        String title = "Role changed!";
        String body = "Your current role is " + person.getRole() + "!";

        Note note = new Note(title, body, new HashMap<>());

        firebaseUtil.sendNotification(note, person, "role");
    }
}
