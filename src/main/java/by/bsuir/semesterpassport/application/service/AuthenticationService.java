package by.bsuir.semesterpassport.application.service;

import by.bsuir.semesterpassport.domain.model.User;
import by.bsuir.semesterpassport.domain.model.Role;
import by.bsuir.semesterpassport.domain.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    // JwtService добавим следующим шагом

    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerStudent(String email, String password, String firstName, String lastName, String group) {
        // Проверка на существование (пользовательские исключения согласно ТЗ) [cite: 502]
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("User with this email already exists");
        }

        User user = new User();
        user.setEmail(email);
        // Хеширование пароля через BCrypt
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setGroupNumber(group);
        user.setRole(Role.STUDENT);

        return userRepository.save(user);
    }

    public Optional<User> authenticate(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(user -> passwordEncoder.matches(password, user.getPasswordHash()));
    }
}