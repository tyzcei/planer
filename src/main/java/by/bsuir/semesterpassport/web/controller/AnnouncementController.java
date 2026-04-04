package by.bsuir.semesterpassport.web.controller;

import by.bsuir.semesterpassport.application.dto.AnnouncementRequest;
import by.bsuir.semesterpassport.application.service.AnnouncementService;
import by.bsuir.semesterpassport.domain.model.Announcement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
// 1. ИСПРАВЛЕНИЕ: Добавили /v1, чтобы совпадало с твоим SecurityConfig и фронтендом
@RequestMapping("/api/v1/announcements")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    public AnnouncementController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    @GetMapping("/{groupNumber}")
    public ResponseEntity<Announcement> getAnnouncement(@PathVariable String groupNumber) {
        return ResponseEntity.ok(announcementService.getAnnouncementByGroup(groupNumber));
    }

    @PutMapping("/{groupNumber}")
    // 2. ИСПРАВЛЕНИЕ: Используем hasAuthority вместо hasRole
    @PreAuthorize("hasAuthority('GROUP_LEADER')")
    public ResponseEntity<Announcement> updateAnnouncement(
            @PathVariable String groupNumber,
            @RequestBody AnnouncementRequest request) {
        return ResponseEntity.ok(announcementService.updateAnnouncement(groupNumber, request.content()));
    }

    // Добавь этот метод в контроллер:
    @PatchMapping("/{groupNumber}/hide")
    @PreAuthorize("hasAuthority('GROUP_LEADER')")
    public ResponseEntity<Void> hideAnnouncement(@PathVariable String groupNumber) {
        announcementService.hideAnnouncement(groupNumber);
        return ResponseEntity.ok().build();
    }
}