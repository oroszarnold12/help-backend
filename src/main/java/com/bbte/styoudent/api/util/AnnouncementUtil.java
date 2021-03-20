package com.bbte.styoudent.api.util;

import com.bbte.styoudent.api.exception.BadRequestException;
import com.bbte.styoudent.api.exception.InternalServerException;
import com.bbte.styoudent.service.AnnouncementService;
import com.bbte.styoudent.service.ServiceException;
import org.springframework.stereotype.Component;

@Component
public class AnnouncementUtil {
    private final AnnouncementService announcementService;

    public AnnouncementUtil(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    public void checkIfHasThisAnnouncement(Long courseId, Long announcementId) {
        try {
            if (!announcementService.checkIfExistsByCourseIdAndId(courseId, announcementId)) {
                throw new BadRequestException(
                        "Course with id: " + courseId + " has no announcement with id: " + announcementId + "!"
                );
            }
        } catch (ServiceException se) {
            throw new InternalServerException("Could not check announcement!", se);
        }
    }
}
