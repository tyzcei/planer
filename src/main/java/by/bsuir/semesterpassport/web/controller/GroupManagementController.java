package by.bsuir.semesterpassport.web.controller;

import by.bsuir.semesterpassport.application.dto.StudentDto;
import by.bsuir.semesterpassport.application.service.GroupManagementService;
import by.bsuir.semesterpassport.domain.model.GroupNotification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/group-management")
@PreAuthorize("hasAuthority('GROUP_LEADER')") // Защищаем весь контроллер сразу!
public class GroupManagementController {

    private final GroupManagementService groupManagementService;

    public GroupManagementController(GroupManagementService groupManagementService) {
        this.groupManagementService = groupManagementService;
    }

    @GetMapping("/{groupNumber}/students")
    public ResponseEntity<List<StudentDto>> getStudents(@PathVariable String groupNumber) {
        return ResponseEntity.ok(groupManagementService.getStudentsByGroup(groupNumber));
    }

    @DeleteMapping("/students/{studentId}")
    public ResponseEntity<Void> removeStudent(@PathVariable Long studentId) {
        groupManagementService.removeStudentFromGroup(studentId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{groupNumber}/notifications")
    public ResponseEntity<List<GroupNotification>> getNotifications(@PathVariable String groupNumber) {
        return ResponseEntity.ok(groupManagementService.getNotifications(groupNumber));
    }

    @PatchMapping("/notifications/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long notificationId) {
        groupManagementService.markNotificationAsRead(notificationId);
        return ResponseEntity.ok().build();
    }
}