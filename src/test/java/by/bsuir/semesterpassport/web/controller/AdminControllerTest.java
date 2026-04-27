package by.bsuir.semesterpassport.web.controller;

import by.bsuir.semesterpassport.domain.model.Role;
import by.bsuir.semesterpassport.domain.model.User;
import by.bsuir.semesterpassport.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    private MockMvc mockMvc;
    @Mock private UserRepository userRepository;
    @InjectMocks private AdminController adminController;

    @BeforeEach void setUp() { mockMvc = MockMvcBuilders.standaloneSetup(adminController).build(); }

    // Тест 25
    @Test
    @DisplayName("updateUserRole: Смена роли на GROUP_LEADER")
    void updateUserRole_Success() throws Exception {
        User user = new User();
        user.setRole(Role.STUDENT);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(patch("/api/v1/admin/users/1/role")
                        .param("newRole", "GROUP_LEADER"))
                .andExpect(status().isOk());
        assertEquals(Role.GROUP_LEADER, user.getRole());
    }

    // Тест 26
    @Test
    @DisplayName("updateUserGroup: Смена учебной группы")
    void updateUserGroup_Success() throws Exception {
        User user = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(patch("/api/v1/admin/users/1/group")
                        .param("groupNumber", "314302"))
                .andExpect(status().isOk());
        assertEquals("314302", user.getGroupNumber());
    }
}