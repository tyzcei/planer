package by.bsuir.semesterpassport.web.controller;

import by.bsuir.semesterpassport.application.service.TeacherService;
import by.bsuir.semesterpassport.application.service.TeacherSyncService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TeacherControllerTest {

    private MockMvc mockMvc;
    @Mock private TeacherService teacherService;
    @Mock private TeacherSyncService teacherSyncService;
    @InjectMocks private TeacherController teacherController;

    @BeforeEach void setUp() { mockMvc = MockMvcBuilders.standaloneSetup(teacherController).build(); }

    // Тест 30
    @Test
    @DisplayName("forceSyncTeachers: Запуск синхронизации с API БГУИР")
    void forceSyncTeachers_Success() throws Exception {
        mockMvc.perform(post("/api/v1/teachers/force-sync"))
                .andExpect(status().isOk());
        // Проверяем, что метод сервиса реально был вызван контроллером
        verify(teacherSyncService).syncTeachersWeekly();
    }
}