package by.bsuir.semesterpassport.web.controller;

import by.bsuir.semesterpassport.application.dto.StudentDto;
import by.bsuir.semesterpassport.application.service.GroupManagementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class GroupManagementControllerTest {

    private MockMvc mockMvc;
    @Mock private GroupManagementService groupManagementService;
    @InjectMocks private GroupManagementController groupManagementController;

    @BeforeEach void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(groupManagementController).build();
    }

    // Тест 21
    @Test
    @DisplayName("getStudents: Успешное получение списка группы")
    void getStudents_Success() throws Exception {
        StudentDto student = new StudentDto(1L, "Иван", "Иванов", "test@test", "STUDENT");
        when(groupManagementService.getStudentsByGroup("314302")).thenReturn(List.of(student));

        mockMvc.perform(get("/api/v1/group-management/314302/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    // Тест 22
    @Test
    @DisplayName("removeStudent: Успешное удаление из группы")
    void removeStudent_Success() throws Exception {
        mockMvc.perform(delete("/api/v1/group-management/students/1"))
                .andExpect(status().isOk());
        verify(groupManagementService).removeStudentFromGroup(1L);
    }
}