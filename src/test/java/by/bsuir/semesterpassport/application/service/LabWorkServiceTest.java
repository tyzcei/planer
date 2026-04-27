package by.bsuir.semesterpassport.application.service;

import by.bsuir.semesterpassport.application.dto.LabWorkRequest;
import by.bsuir.semesterpassport.domain.model.LabStatus;
import by.bsuir.semesterpassport.domain.model.LabWork;
import by.bsuir.semesterpassport.domain.model.Subject;
import by.bsuir.semesterpassport.domain.model.User;
import by.bsuir.semesterpassport.domain.repository.LabWorkRepository;
import by.bsuir.semesterpassport.domain.repository.SubjectRepository;
import by.bsuir.semesterpassport.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LabWorkServiceTest {

    @Mock
    private LabWorkRepository labWorkRepository;
    @Mock
    private PrioritySorterService prioritySorterService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SubjectRepository subjectRepository;

    @InjectMocks
    private LabWorkService labWorkService;

    private User testUser;
    private Subject testSubject;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setGroupNumber("123456");

        testSubject = new Subject();
        testSubject.setSubjectId(1L);
        testSubject.setTitle("ОАиП");
    }

    // Тест 6
    @Test
    @DisplayName("createLab: Успешное создание лабораторной работы")
    void createLab_Success() {
        LabWorkRequest request = new LabWorkRequest();
        request.setTitle("Лаба 1");
        request.setComplexity(3);
        request.setUserId(1L);
        request.setSubjectId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(testSubject));
        when(labWorkRepository.save(any(LabWork.class))).thenAnswer(i -> i.getArguments()[0]);

        LabWork createdLab = labWorkService.createLab(request);

        assertNotNull(createdLab);
        assertEquals("Лаба 1", createdLab.getTitle());
        assertEquals(LabStatus.RECEIVED, createdLab.getCurrentStatus());
        verify(labWorkRepository, times(1)).save(any(LabWork.class));
    }

    // Тест 7
    @Test
    @DisplayName("toggleStatus: Цикличное переключение RECEIVED -> CODED")
    void toggleStatus_ReceivedToCoded() {
        LabWork lab = new LabWork();
        lab.setCurrentStatus(LabStatus.RECEIVED);

        when(labWorkRepository.findByLabIdAndUserUserId(10L, 1L)).thenReturn(Optional.of(lab));
        when(labWorkRepository.save(any(LabWork.class))).thenAnswer(i -> i.getArguments()[0]);

        LabWork updatedLab = labWorkService.toggleStatus(10L, 1L);

        assertEquals(LabStatus.CODED, updatedLab.getCurrentStatus());
    }

    // Тест 8
    @Test
    @DisplayName("toggleStatus: Цикличное переключение PROTECTED -> RECEIVED")
    void toggleStatus_ProtectedToReceived() {
        LabWork lab = new LabWork();
        lab.setCurrentStatus(LabStatus.PROTECTED);

        when(labWorkRepository.findByLabIdAndUserUserId(10L, 1L)).thenReturn(Optional.of(lab));
        when(labWorkRepository.save(any(LabWork.class))).thenAnswer(i -> i.getArguments()[0]);

        LabWork updatedLab = labWorkService.toggleStatus(10L, 1L);

        assertEquals(LabStatus.RECEIVED, updatedLab.getCurrentStatus(), "Статус должен сброситься в начало");
    }

    // Тест 9
    @Test
    @DisplayName("getStudentLabsSorted: Вызов сортировщика")
    void getStudentLabsSorted_CallsPrioritySorter() {
        List<LabWork> mockLabs = Arrays.asList(new LabWork(), new LabWork());
        when(labWorkRepository.findAllByUserUserId(1L)).thenReturn(mockLabs);
        when(prioritySorterService.sortLabsByPriority(mockLabs)).thenReturn(mockLabs);

        List<LabWork> result = labWorkService.getStudentLabsSorted(1L);

        assertEquals(2, result.size());
        verify(prioritySorterService, times(1)).sortLabsByPriority(mockLabs);
    }

    // Тест 10
    @Test
    @DisplayName("broadcastLabToGroup: Успешное массовое назначение работ")
    void broadcastLabToGroup_Success() {
        User student1 = new User();
        User student2 = new User();
        List<User> students = Arrays.asList(student1, student2);

        LabWorkRequest request = new LabWorkRequest();
        request.setTitle("Массовая Лаба");
        request.setSubjectId(1L);
        request.setDeadline(LocalDateTime.now().plusDays(7));

        when(userRepository.findAllByGroupNumber("123456")).thenReturn(students);
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(testSubject));

        labWorkService.broadcastLabToGroup(request, "123456");

        // Проверяем, что метод saveAll был вызван один раз со списком из 2 элементов
        verify(labWorkRepository, times(1)).saveAll(argThat(list -> ((List<?>) list).size() == 2));
    }
}