package by.bsuir.semesterpassport.application.service;

import by.bsuir.semesterpassport.domain.model.Announcement;
import by.bsuir.semesterpassport.domain.repository.AnnouncementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;

    public AnnouncementService(AnnouncementRepository announcementRepository) {
        this.announcementRepository = announcementRepository;
    }

    public Announcement getAnnouncementByGroup(String groupNumber) {
        return announcementRepository.findByGroupNumber(groupNumber)
                .orElse(new Announcement("Объявлений пока нет", groupNumber, LocalDateTime.now()));
    }

    @Transactional
    public Announcement updateAnnouncement(String groupNumber, String content) {
        // Ищем существующее или создаем новое
        Announcement announcement = announcementRepository.findByGroupNumber(groupNumber)
                .orElse(new Announcement(content, groupNumber, LocalDateTime.now()));

        announcement.setContent(content);
        announcement.setUpdatedAt(LocalDateTime.now());

        return announcementRepository.save(announcement);
    }
}