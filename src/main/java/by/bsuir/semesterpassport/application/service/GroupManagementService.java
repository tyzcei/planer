package by.bsuir.semesterpassport.application.service;

import by.bsuir.semesterpassport.application.dto.StudentDto;
import by.bsuir.semesterpassport.domain.model.GroupNotification;
import by.bsuir.semesterpassport.domain.model.User;
import by.bsuir.semesterpassport.domain.repository.GroupNotificationRepository;
import by.bsuir.semesterpassport.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupManagementService {

    private final UserRepository userRepository;
    private final GroupNotificationRepository notificationRepository;

    public GroupManagementService(UserRepository userRepository, GroupNotificationRepository notificationRepository) {
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
    }

    // 1. Получить список студентов группы
    // 1. Получить список студентов группы
    public List<StudentDto> getStudentsByGroup(String groupNumber) {
        return userRepository.findAllByGroupNumber(groupNumber).stream()
                .map(user -> new StudentDto(
                        user.getUserId(), // Исправил на getUserId(), как в твоем классе User
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                        user.getRole().name() // <--- ИСПРАВЛЕНИЕ ЗДЕСЬ: добавили .name()
                ))
                .collect(Collectors.toList());
    }

    // 2. Исключить студента из группы (отвязываем его от группы)
    @Transactional
    public void removeStudentFromGroup(Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Студент не найден"));

        // Просто зануляем номер группы, не удаляя самого юзера из базы
        student.setGroupNumber(null);
        userRepository.save(student);
    }

    // 3. Получить уведомления
    public List<GroupNotification> getNotifications(String groupNumber) {
        return notificationRepository.findByGroupNumberOrderByCreatedAtDesc(groupNumber);
    }

    // 4. Отметить уведомление как прочитанное
    @Transactional
    public void markNotificationAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
        });
    }
}