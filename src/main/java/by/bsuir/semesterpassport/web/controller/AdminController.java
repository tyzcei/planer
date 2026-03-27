package by.bsuir.semesterpassport.web.controller;

import by.bsuir.semesterpassport.domain.model.Role;
import by.bsuir.semesterpassport.domain.model.User;
import by.bsuir.semesterpassport.domain.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@CrossOrigin("*")
public class AdminController {

    private final UserRepository userRepository;

    public AdminController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 1. Получить список всех зарегистрированных пользователей
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    // 2. Сменить роль (например, сделать STUDENT -> GROUP_LEADER)
    @PatchMapping("/users/{id}/role")
    public ResponseEntity<Void> updateUserRole(
            @PathVariable Long id,
            @RequestParam Role newRole) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        user.setRole(newRole);
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    // 3. Назначить/сменить группу (нужно для старост)
    @PatchMapping("/users/{id}/group")
    public ResponseEntity<Void> updateUserGroup(
            @PathVariable Long id,
            @RequestParam String groupNumber) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        user.setGroupNumber(groupNumber);
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }
}