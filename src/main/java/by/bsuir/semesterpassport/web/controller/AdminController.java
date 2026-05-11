package by.bsuir.semesterpassport.web.controller;

import by.bsuir.semesterpassport.domain.model.GroupRequest;
import by.bsuir.semesterpassport.domain.model.RequestStatus;
import by.bsuir.semesterpassport.domain.model.Role;
import by.bsuir.semesterpassport.domain.model.User;
import by.bsuir.semesterpassport.domain.repository.GroupRequestRepository;
import by.bsuir.semesterpassport.domain.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final GroupRequestRepository groupRequestRepository; // НОВОЕ ПОЛЕ

    public AdminController(UserRepository userRepository, GroupRequestRepository groupRequestRepository) {
        this.userRepository = userRepository;
        this.groupRequestRepository = groupRequestRepository;
    }

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @PatchMapping("/users/{id}/role")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> updateUserRole(@PathVariable Long id, @RequestParam String newRole) {
        return userRepository.findById(id).map(user -> {
            user.setRole(Role.valueOf(newRole.toUpperCase()));
            userRepository.save(user);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/users/{id}/group")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> updateUserGroup(@PathVariable Long id, @RequestParam String groupNumber) {
        return userRepository.findById(id).map(user -> {
            user.setGroupNumber(groupNumber);
            userRepository.save(user);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/users/{id}/block")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> toggleBlockUser(@PathVariable Long id, @RequestParam boolean block) {
        return userRepository.findById(id).map(user -> {
            user.setBlocked(block);
            userRepository.save(user);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) return ResponseEntity.notFound().build();
        userRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/groups")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<String>> getAllGroups() {
        return ResponseEntity.ok(userRepository.findDistinctGroupNumbers());
    }

    // ==========================================
    // НОВЫЕ ЭНДПОИНТЫ ДЛЯ ЗАПРОСОВ В ГРУППУ
    // ==========================================

    @GetMapping("/group-requests")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<GroupRequestDto>> getPendingRequests() {
        // Достаем только те, что со статусом PENDING
        List<GroupRequestDto> requests = groupRequestRepository.findByStatusOrderByCreatedAtDesc(RequestStatus.PENDING)
                .stream()
                .map(r -> new GroupRequestDto(
                        r.getId(),
                        r.getUser().getEmail(),
                        r.getUser().getFirstName() + " " + r.getUser().getLastName(),
                        r.getRequestedGroupNumber(),
                        r.getCreatedAt()
                )).toList();
        return ResponseEntity.ok(requests);
    }

    @PatchMapping("/group-requests/{requestId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> processGroupRequest(@PathVariable Long requestId, @RequestParam boolean approve) {
        GroupRequest request = groupRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Запрос не найден"));

        if (approve) {
            request.setStatus(RequestStatus.APPROVED);
            // Сохраняем группу пользователю
            User user = request.getUser();
            user.setGroupNumber(request.getRequestedGroupNumber());
            userRepository.save(user);
        } else {
            request.setStatus(RequestStatus.REJECTED);
        }

        groupRequestRepository.save(request);
        return ResponseEntity.ok().build();
    }
}

// DTO для удобной отправки на фронтенд
record GroupRequestDto(Long id, String email, String fullName, String requestedGroup, LocalDateTime createdAt) {}