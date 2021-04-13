package com.bbte.styoudent.api.util;

import com.bbte.styoudent.model.Course;
import com.bbte.styoudent.model.Invitation;
import com.bbte.styoudent.model.Note;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class InvitationUtil {
    private final FirebaseUtil firebaseUtil;

    public InvitationUtil(FirebaseUtil firebaseUtil) {
        this.firebaseUtil = firebaseUtil;
    }

    private Note createDataForInvitationNotification(String title, String body) {
        Map<String, String> data = new HashMap<>();
        data.put("forInvitation", "true");

        return new Note(title, body, data);
    }

    public void createSingleNotificationForInvitation(Invitation invitation) {
        Course course = invitation.getCourse();
        String title = "New invitation";
        String body = "You have been invited to " + course.getName();

        Note note = createDataForInvitationNotification(title, body);

        firebaseUtil.sendNotification(note, invitation.getPerson(), "invitation");
    }
}
