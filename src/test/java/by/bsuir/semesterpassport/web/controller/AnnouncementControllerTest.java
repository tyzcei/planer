package by.bsuir.semesterpassport.web.controller;

import by.bsuir.semesterpassport.application.dto.AnnouncementRequest;
import by.bsuir.semesterpassport.application.service.AnnouncementService;
import by.bsuir.semesterpassport.domain.model.Announcement;
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

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AnnouncementControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();
    @Mock private AnnouncementService announcementService;
    @InjectMocks private AnnouncementController announcementController;

    @BeforeEach void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(announcementController).build();
    }

    // Тест 23
    @Test
    @DisplayName("updateAnnouncement: Публикация объявления (200 OK)")
    void updateAnnouncement_Success() throws Exception {
        AnnouncementRequest request = new AnnouncementRequest("Завтра пары не будет");
        Announcement mockAnn = new Announcement("Завтра пары не будет", "314302", LocalDateTime.now());

        when(announcementService.updateAnnouncement(eq("314302"), anyString())).thenReturn(mockAnn);

        mockMvc.perform(put("/api/v1/announcements/314302")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    // Тест 24 (Негативный)
    @Test
    @DisplayName("updateAnnouncement: Пустое тело запроса (400 Bad Request)")
    void updateAnnouncement_EmptyBody() throws Exception {
        mockMvc.perform(put("/api/v1/announcements/314302")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}