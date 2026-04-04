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
        Announcement announcement = announcementRepository.findByGroupNumber(groupNumber)
                .orElse(new Announcement(content, groupNumber, LocalDateTime.now()));

        announcement.setContent(content);
        announcement.setUpdatedAt(LocalDateTime.now());
        announcement.setActive(true); // <--- ВАЖНО: Если староста пишет новое, оно снова активно

        return announcementRepository.save(announcement);
    }

    // НОВЫЙ МЕТОД: Скрываем объявление
    @Transactional
    public void hideAnnouncement(String groupNumber) {
        announcementRepository.findByGroupNumber(groupNumber).ifPresent(announcement -> {
            announcement.setActive(false);
            announcementRepository.save(announcement);
        });
    }
}