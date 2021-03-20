package com.bbte.styoudent.api.util;

import com.bbte.styoudent.api.exception.BadRequestException;
import com.bbte.styoudent.api.exception.InternalServerException;
import com.bbte.styoudent.service.DiscussionService;
import com.bbte.styoudent.service.ServiceException;
import org.springframework.stereotype.Component;

@Component
public class DiscussionUtil {
    private final DiscussionService discussionService;

    public DiscussionUtil(DiscussionService discussionService) {
        this.discussionService = discussionService;
    }

    public void checkIfHasThisDiscussion(Long courseId, Long discussionId) {
        try {
            if (!discussionService.checkIfExistsByCourseIdAndId(courseId, discussionId)) {
                throw new BadRequestException(
                        "Course with id: " + courseId + " has no discussion with id: " + discussionId + "!"
                );
            }
        } catch (ServiceException se) {
            throw new InternalServerException("Could not check discussion!", se);
        }
    }
}
