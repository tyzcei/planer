package by.bsuir.semesterpassport.infrastructure.config;

import by.bsuir.semesterpassport.application.service.JwtService;
import by.bsuir.semesterpassport.domain.model.Role;
import by.bsuir.semesterpassport.domain.model.User;
import by.bsuir.semesterpassport.domain.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public OAuth2LoginSuccessHandler(UserRepository userRepository, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // Достаем email из GitHub (если он скрыт, берем логин)
        String email = oAuth2User.getAttribute("email");
        if (email == null) {
            String login = oAuth2User.getAttribute("login");
            email = login + "@github.com";
        }

        String name = oAuth2User.getAttribute("name");
        if (name == null) name = "GitHub User";

        String[] names = name.split(" ");
        String firstName = names[0];
        String lastName = names.length > 1 ? names[1] : "";

        // Ищем юзера или создаем нового
        final String finalEmail = email;
        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(finalEmail);
            newUser.setFirstName(firstName);
            newUser.setLastName(lastName);
            newUser.setRole(Role.STUDENT);
            // Генерируем случайный невозможный пароль, т.к. вход через GitHub
            newUser.setPasswordHash(passwordEncoder.encode(UUID.randomUUID().toString()));
            return userRepository.save(newUser);
        });

        // Генерируем наш JWT и отправляем на фронтенд
        String token = jwtService.generateToken(user);
        response.sendRedirect("http://localhost:5173/?token=" + token);
    }
}