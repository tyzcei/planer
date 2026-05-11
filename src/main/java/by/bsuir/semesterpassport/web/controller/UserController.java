package by.bsuir.semesterpassport.web.controller;

import by.bsuir.semesterpassport.domain.model.GroupRequest;
import by.bsuir.semesterpassport.domain.model.RequestStatus;
import by.bsuir.semesterpassport.domain.model.User;
import by.bsuir.semesterpassport.domain.repository.GroupRequestRepository;
import by.bsuir.semesterpassport.domain.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final GroupRequestRepository groupRequestRepository; // НОВОЕ

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder, GroupRequestRepository groupRequestRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.groupRequestRepository = groupRequestRepository;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserProfile(@PathVariable Long userId) {
        return userRepository.findById(userId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{userId}/update-info")
    public ResponseEntity<?> updateUserInfo(@PathVariable Long userId, @RequestBody UpdateInfoRequest request) {
        return userRepository.findById(userId).map(user -> {
            if (request.firstName() != null) user.setFirstName(request.firstName());
            if (request.lastName() != null) user.setLastName(request.lastName());
            userRepository.save(user);
            return ResponseEntity.ok(user);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{userId}/change-password")
    public ResponseEntity<?> changePassword(@PathVariable Long userId, @RequestBody ChangePasswordRequest request) {
        if (request.newPassword() == null || request.newPassword().isBlank()) {
            return ResponseEntity.badRequest().body("Пароль не может быть пустым");
        }
        if (request.newPassword().length() < 4) {
            return ResponseEntity.badRequest().body("Пароль слишком короткий (мин. 4 символа)");
        }
        return userRepository.findById(userId).map(user -> {
            user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
            userRepository.save(user);
            return ResponseEntity.ok().body(Map.of("message", "Пароль успешно изменен"));
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
        return ResponseEntity.ok(userRepository.existsByEmail(email));
    }

    // ==========================================
    // НОВЫЙ ЭНДПОИНТ: Отправка запроса в группу
    // ==========================================
    @PostMapping("/{userId}/group-request")
    public ResponseEntity<?> requestGroup(@PathVariable Long userId, @RequestBody GroupRequestPayload payload) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        // Проверяем, нет ли уже активного запроса
        if (groupRequestRepository.existsByUserUserIdAndStatus(userId, RequestStatus.PENDING)) {
            return ResponseEntity.badRequest().body(Map.of("message", "У вас уже есть активный запрос. Дождитесь ответа администратора."));
        }

        GroupRequest request = new GroupRequest();
        request.setUser(user);
        request.setRequestedGroupNumber(payload.groupNumber());
        request.setStatus(RequestStatus.PENDING);
        request.setCreatedAt(LocalDateTime.now());

        groupRequestRepository.save(request);

        return ResponseEntity.ok(Map.of("message", "Запрос успешно отправлен администратору!"));
    }
}

record UpdateInfoRequest(String firstName, String lastName) {}
record ChangePasswordRequest(String newPassword) {}
record GroupRequestPayload(String groupNumber) {} // DTO для запроса