package by.bsuir.semesterpassport.web.controller;

import by.bsuir.semesterpassport.domain.model.Role;
import by.bsuir.semesterpassport.domain.model.User;
import by.bsuir.semesterpassport.domain.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
// ❌ ВАЖНО: Мы убрали @CrossOrigin("*"), чтобы не было конфликта с SecurityConfig!
public class AdminController {

    private final UserRepository userRepository;

    public AdminController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 1. Получить список всех пользователей
    @GetMapping("/users")
    @PreAuthorize("hasAuthority('ADMIN')") // Явная защита метода
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    // 2. Сменить роль
    @PatchMapping("/users/{id}/role")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> updateUserRole(
            @PathVariable Long id,
            @RequestParam String newRole) { // Принимаем String вместо Role!

        return userRepository.findById(id).map(user -> {
            // Конвертируем строку в Enum безопасно.
            // (Если в классе User поле role имеет тип String, то просто user.setRole(newRole))
            user.setRole(Role.valueOf(newRole.toUpperCase()));

            userRepository.save(user);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }

    // 3. Назначить/сменить группу
    @PatchMapping("/users/{id}/group")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> updateUserGroup(
            @PathVariable Long id,
            @RequestParam String groupNumber) {

        return userRepository.findById(id).map(user -> {
            user.setGroupNumber(groupNumber);
            userRepository.save(user);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }
}