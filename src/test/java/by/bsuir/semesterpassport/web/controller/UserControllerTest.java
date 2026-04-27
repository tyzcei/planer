package by.bsuir.semesterpassport.web.controller;

import by.bsuir.semesterpassport.domain.model.User;
import by.bsuir.semesterpassport.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @InjectMocks private UserController userController;

    @BeforeEach void setUp() { mockMvc = MockMvcBuilders.standaloneSetup(userController).build(); }

    // Тест 27



    // Тест 29
    @Test
    @DisplayName("changePassword: Успешная смена пароля")
    void changePassword_Success() throws Exception {
        User user = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("123456")).thenReturn("hashedPass");

        mockMvc.perform(put("/api/v1/users/1/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"newPassword\":\"123456\"}"))
                .andExpect(status().isOk());
    }
}