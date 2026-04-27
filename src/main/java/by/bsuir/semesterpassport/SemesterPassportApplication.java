package by.bsuir.semesterpassport;

import by.bsuir.semesterpassport.domain.model.Role;
import by.bsuir.semesterpassport.domain.model.User;
import by.bsuir.semesterpassport.domain.repository.UserRepository;
import by.bsuir.semesterpassport.domain.model.Subject;
import by.bsuir.semesterpassport.domain.repository.LabWorkRepository;
import by.bsuir.semesterpassport.domain.repository.SubjectRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@SpringBootApplication
@EnableScheduling
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

            // Ищем пользователя, если нет - создаем, если есть - ПРИНУДИТЕЛЬНО обновляем пароль
            User user = userRepository.findByEmail(email).orElse(new User());
            user.setEmail(email);
            user.setFirstName("Alexandra");
            user.setLastName("Lapteva");
            user.setGroupNumber("314302");

            // ❌ БЫЛО: user.setRole(Role.STUDENT);
            // ✅ СТАЛО: Делаем тебя главным админом!
            user.setRole(Role.ADMIN);

            // ПЕРЕЗАПИСЫВАЕМ ПАРОЛЬ ПРИ КАЖДОМ СТАРТЕ (для тестов)
            user.setPasswordHash(passwordEncoder.encode("a"));
            userRepository.save(user);

            System.out.println(">>> Пользователь a@a готов. Роль: ADMIN. Пароль: 'a'");

            // 1. Создаем тестовый предмет
            if (subjectRepository.count() == 0) {
                Subject ris = new Subject();
                ris.setTitle("РИS");
                ris.setControlType("ЭКЗАМЕН");
                subjectRepository.save(ris);
            }
        };
    }
}