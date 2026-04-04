package by.bsuir.semesterpassport.web.controller;

import by.bsuir.semesterpassport.domain.model.User;
import by.bsuir.semesterpassport.domain.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Получить полные данные пользователя
     */
    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserProfile(@PathVariable Long userId) {
        return userRepository.findById(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 1. ОБНОВЛЕНИЕ ТОЛЬКО ИНФОРМАЦИИ (Имя, Фамилия)
     */
    @PutMapping("/{userId}/update-info")
    public ResponseEntity<?> updateUserInfo(
            @PathVariable Long userId,
            @RequestBody UpdateInfoRequest request) {

        return userRepository.findById(userId)
                .map(user -> {
                    if (request.firstName() != null) user.setFirstName(request.firstName());
                    if (request.lastName() != null) user.setLastName(request.lastName());
                    userRepository.save(user);
                    return ResponseEntity.ok(user);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 2. ОТДЕЛЬНАЯ СМЕНА ПАРОЛЯ
     */
    @PutMapping("/{userId}/change-password")
    public ResponseEntity<?> changePassword(
            @PathVariable Long userId,
            @RequestBody ChangePasswordRequest request) {

        if (request.newPassword() == null || request.newPassword().isBlank()) {
            return ResponseEntity.badRequest().body("Пароль не может быть пустым");
        }

        if (request.newPassword().length() < 4) {
            return ResponseEntity.badRequest().body("Пароль слишком короткий (мин. 4 символа)");
        }

        return userRepository.findById(userId)
                .map(user -> {
                    user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
                    userRepository.save(user);
                    return ResponseEntity.ok().body(Map.of("message", "Пароль успешно изменен"));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Проверка существования email
     */
    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
        return ResponseEntity.ok(userRepository.existsByEmail(email));
    }
}

/**
 * DTO для обновления текстовых полей
 */
record UpdateInfoRequest(String firstName, String lastName) {}

/**
 * DTO для смены пароля
 */
record ChangePasswordRequest(String newPassword) {}