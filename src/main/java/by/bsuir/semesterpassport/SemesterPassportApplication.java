package by.bsuir.semesterpassport;

import by.bsuir.semesterpassport.domain.model.Role;
import by.bsuir.semesterpassport.domain.model.User;
import by.bsuir.semesterpassport.domain.repository.UserRepository;
import by.bsuir.semesterpassport.domain.repository.LabWorkRepository;
import by.bsuir.semesterpassport.domain.repository.SubjectRepository;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableScheduling
@EnableCaching
public class SemesterPassportApplication {

    public static void main(String[] args) {
        SpringApplication.run(SemesterPassportApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String email = "a@a";

            // Ищем пользователя, если нет - создаем, если есть - ПРИНУДИТЕЛЬНО обновляем пароль
            User user = userRepository.findByEmail(email).orElse(new User());
            user.setEmail(email);
            user.setFirstName("Alexandra");
            user.setLastName("Lapteva");
            user.setGroupNumber("314302");
            user.setRole(Role.ADMIN);

            // ПЕРЕЗАПИСЫВАЕМ ПАРОЛЬ ПРИ КАЖДОМ СТАРТЕ (для тестов)
            user.setPasswordHash(passwordEncoder.encode("a"));
            userRepository.save(user);

            System.out.println(">>> Пользователь a@a готов. Роль: ADMIN. Пароль: 'a'");

            // Тестовый предмет мы удалили, потому что теперь предметы
            // подтягиваются из БГУИР автоматически!
        };
    }
}