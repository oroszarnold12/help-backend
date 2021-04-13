package com.bbte.styoudent.api.util;

import com.bbte.styoudent.api.exception.InternalServerException;
import com.bbte.styoudent.api.exception.NotFoundException;
import com.bbte.styoudent.model.CourseFile;
import com.bbte.styoudent.service.CourseFileService;
import com.bbte.styoudent.service.ServiceException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class CourseUtil {
    private final CourseFileService courseFileService;

    public CourseUtil(CourseFileService courseFileService) {
        this.courseFileService = courseFileService;
    }

    public static String getCorrectedFileName(List<CourseFile> files, String oldFileName) {
        int i = 1;
        final AtomicReference<String> newFileName = new AtomicReference<>();

        newFileName.set(oldFileName);

        while (files.stream().anyMatch((file) -> file.getFileName().equals(newFileName.get()))) {
            newFileName.set(oldFileName + "-" + i);
            i++;
        }

        return newFileName.get();
    }

    public void checkIfHasThisFile(Long courseId, Long fileId) {
        try {
            if (!courseFileService.checkIfExistsByCourseIdAndId(courseId, fileId)) {
                throw new NotFoundException(
                        "Course with id: " + courseId + " has no file with id: " + fileId + "!"
                );
            }
        } catch (ServiceException se) {
            throw new InternalServerException("Could not check course file!", se);
        }
    }
}
