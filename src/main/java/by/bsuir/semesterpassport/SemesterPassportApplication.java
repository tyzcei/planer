package by.bsuir.semesterpassport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import by.bsuir.semesterpassport.domain.model.Role;
import by.bsuir.semesterpassport.domain.model.User;
import by.bsuir.semesterpassport.domain.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import by.bsuir.semesterpassport.domain.model.*;
import by.bsuir.semesterpassport.domain.repository.*;

import java.time.LocalDateTime;

@SpringBootApplication
public class SemesterPassportApplication {

    public static void main(String[] args) {
        SpringApplication.run(SemesterPassportApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(UserRepository userRepository,
                                      SubjectRepository subjectRepository,
                                      LabWorkRepository labWorkRepository,
                                      PasswordEncoder passwordEncoder) {
        return args -> {
            String email = "a@a";
            User user = userRepository.findByEmail(email).orElseGet(() -> {
                User u = new User();
                u.setEmail(email);
                u.setFirstName("Alexandra");
                u.setPasswordHash(passwordEncoder.encode("a"));
                u.setRole(Role.STUDENT);
                return userRepository.save(u);
            });

            // 1. Создаем предмет, если его нет
            Subject ris = subjectRepository.findAll().stream()
                    .filter(s -> s.getTitle().equals("РИS"))
                    .findFirst()
                    .orElseGet(() -> {
                        Subject s = new Subject();
                        s.setTitle("РИS");
                        s.setControlType("ЭКЗАМЕН");
                        return subjectRepository.save(s);
                    });

            // 2. Создаем тестовые лабы, если у юзера пусто
            if (labWorkRepository.findAllByUserUserId(user.getUserId()).isEmpty()) {
                // Лаба 1: Сложная, дедлайн через месяц (Низкий приоритет)
                createLab(labWorkRepository, user, ris, "Лабораторная №1", 5, LocalDateTime.now().plusMonths(1));

                // Лаба 2: Легкая, но дедлайн ЗАВТРА (Высокий приоритет!)
                createLab(labWorkRepository, user, ris, "Лабораторная №2", 1, LocalDateTime.now().plusDays(1));

                // Лаба 3: Средняя, дедлайн через неделю
                createLab(labWorkRepository, user, ris, "Лабораторная №3", 3, LocalDateTime.now().plusWeeks(1));

                System.out.println(">>> Тестовые лабы для Dashboard созданы!");
            }
        };
    }

    // Вспомогательный метод
    private void createLab(LabWorkRepository repo, User user, Subject sub, String title, int complexity, LocalDateTime deadline) {
        LabWork lab = new LabWork();
        lab.setUser(user);
        lab.setSubject(sub);
        lab.setTitle(title);
        lab.setComplexity(complexity);
        lab.setDeadline(deadline);
        lab.setCurrentStatus(LabStatus.RECEIVED);
        repo.save(lab);
    }

}