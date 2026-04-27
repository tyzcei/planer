package by.bsuir.semesterpassport.web.controller;

import by.bsuir.semesterpassport.application.dto.LoginRequest;
import by.bsuir.semesterpassport.application.dto.RegisterRequest;
import by.bsuir.semesterpassport.application.service.AuthenticationService;
import by.bsuir.semesterpassport.application.service.JwtService;
import by.bsuir.semesterpassport.domain.model.Role;
import by.bsuir.semesterpassport.domain.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private AuthenticationService authService;

    @Mock
    private JwtService jwtService; // Добавили мок для JwtService, так как он используется в контроллере

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    // Тест 11
    @Test
    @DisplayName("login: Успешная аутентификация (200 OK)")
    void login_Success() throws Exception {
        // Используем конструктор Record вместо сеттеров
        LoginRequest request = new LoginRequest("student@test.com", "password123");

        User mockUser = new User();
        mockUser.setEmail("student@test.com");
        mockUser.setRole(Role.STUDENT);

        // Настраиваем поведение моков
        when(authService.authenticate(anyString(), anyString())).thenReturn(Optional.of(mockUser));
        when(jwtService.generateToken(any(User.class))).thenReturn("mocked-jwt-token");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("mocked-jwt-token"));
    }

    // Тест 12
    @Test
    @DisplayName("register: Успешная регистрация (200 OK)")
    void register_Success() throws Exception {
        // Конструктор Record
        RegisterRequest request = new RegisterRequest("new@test.com", "12345678", "Иван", "Иванов", "123456");

        User mockUser = new User();
        mockUser.setEmail("new@test.com");
        mockUser.setRole(Role.STUDENT);

        when(authService.registerStudent(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(mockUser);
        when(jwtService.generateToken(any(User.class))).thenReturn("new-jwt-token");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-jwt-token"));
    }

    // Тест 13
    @Test
    @DisplayName("login: Неверные учетные данные (401 Unauthorized)")
    void login_InvalidCredentials_ReturnsUnauthorized() throws Exception {
        LoginRequest request = new LoginRequest("wrong@test.com", "wrongpass");

        // Имитируем, что сервис не нашел юзера (возвращает Optional.empty)
        when(authService.authenticate(anyString(), anyString())).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized()); // Ожидаем HTTP 401
    }

    // Тест 14
    @Test
    @DisplayName("register: Email уже занят (400 Bad Request)")
    void register_EmailTaken_ReturnsBadRequest() throws Exception {
        RegisterRequest request = new RegisterRequest("exist@test.com", "12345678", "Иван", "Иванов", "123456");

        // Имитируем ошибку в сервисе при дубликате email
        when(authService.registerStudent(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("User with this email already exists"));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest()); // Контроллер ловит RuntimeException и возвращает 400
    }

    // Тест 15
    // Тест 15
    @Test
    @DisplayName("login: Отправка некорректного JSON вызывает ошибку (400 Bad Request)")
    void login_MalformedJson() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        // Разорвали строку плюсом, чтобы IDEA не пыталась валидировать этот сломанный JSON
                        .content("{ \"email\": \"test@test.com\", \"password\": " + "}"))
                .andExpect(status().isBadRequest());
    }
}