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
public class AdminController {

    private final UserRepository userRepository;

    public AdminController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 1. Получить список всех пользователей
    @GetMapping("/users")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    // 2. Сменить роль
    @PatchMapping("/users/{id}/role")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> updateUserRole(
            @PathVariable Long id,
            @RequestParam String newRole) {

        return userRepository.findById(id).map(user -> {
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

    // 4. Заблокировать / Разблокировать пользователя
    @PatchMapping("/users/{id}/block")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> toggleBlockUser(
            @PathVariable Long id,
            @RequestParam boolean block) {

        return userRepository.findById(id).map(user -> {
            user.setBlocked(block);
            userRepository.save(user);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }

    // 5. Удалить пользователя (Навсегда)
    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        userRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // 6. Получить список всех уникальных групп
    @GetMapping("/groups")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<String>> getAllGroups() {
        return ResponseEntity.ok(userRepository.findDistinctGroupNumbers());
    }
}