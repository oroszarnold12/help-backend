package com.bbte.styoudent.service.impl;

import com.bbte.styoudent.model.Announcement;
import com.bbte.styoudent.repository.AnnouncementRepository;
import com.bbte.styoudent.service.AnnouncementService;
import com.bbte.styoudent.service.ServiceException;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
public class AnnouncementServiceImpl implements AnnouncementService {
    private final AnnouncementRepository announcementRepository;

    public AnnouncementServiceImpl(AnnouncementRepository announcementRepository) {
        this.announcementRepository = announcementRepository;
    }

    @Override
    public Announcement getByCourseIdAndId(Long courseId, Long id) {
        return announcementRepository.findByCourseIdAndId(courseId, id).orElseThrow(() ->
                new ServiceException("Announcement selection with id: " + id + " failed!"));
    }

    @Override
    public Announcement save(Announcement announcement) {
        try {
            return announcementRepository.save(announcement);
        } catch (DataAccessException de) {
            throw new ServiceException("Announcement insertion failed!", de);
        }
    }

    @Override
    public void delete(Long id) {
        try {
            announcementRepository.deleteById(id);
        } catch (DataAccessException de) {
            throw new ServiceException("Announcement deletion with id: " + id + " failed!", de);
        }
    }

    @Override
    public boolean checkIfExistsByCourseIdAndId(Long courseId, Long id) {
        try {
            return announcementRepository.existsByCourseIdAndId(courseId, id);
        } catch (DataAccessException de) {
            throw new ServiceException("Announcement checking failed!", de);
        }
    }
}
