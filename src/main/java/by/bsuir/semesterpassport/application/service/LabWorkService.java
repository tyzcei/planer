package by.bsuir.semesterpassport.application.service;

import by.bsuir.semesterpassport.application.dto.LabWorkRequest;
import by.bsuir.semesterpassport.domain.model.LabWork;
import by.bsuir.semesterpassport.domain.model.LabStatus;
import by.bsuir.semesterpassport.domain.model.Subject;
import by.bsuir.semesterpassport.domain.model.User;
import by.bsuir.semesterpassport.domain.repository.LabWorkRepository;
import by.bsuir.semesterpassport.domain.repository.SubjectRepository;
import by.bsuir.semesterpassport.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LabWorkService {

    private final LabWorkRepository labWorkRepository;
    private final PrioritySorterService prioritySorterService;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;

    public LabWorkService(LabWorkRepository labWorkRepository,
                          PrioritySorterService prioritySorterService,
                          UserRepository userRepository,
                          SubjectRepository subjectRepository) {
        this.labWorkRepository = labWorkRepository;
        this.prioritySorterService = prioritySorterService;
        this.userRepository = userRepository;
        this.subjectRepository = subjectRepository;
    }

    public List<LabWork> getStudentLabsSorted(Long userId) {
        List<LabWork> labs = labWorkRepository.findAllByUserUserId(userId);
        return prioritySorterService.sortLabsByPriority(labs);
    }

    @Transactional
    public LabWork createLab(LabWorkRequest request) {
        LabWork lab = new LabWork();
        lab.setTitle(request.getTitle());
        lab.setComplexity(request.getComplexity());

        lab.setUser(userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Студент не найден")));

        lab.setSubject(subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Предмет не найден")));

        lab.setDeadline(request.getDeadline() != null ? request.getDeadline() : LocalDateTime.now().plusWeeks(2));
        lab.setCurrentStatus(LabStatus.RECEIVED);

        return labWorkRepository.save(lab);
    }

    @Transactional
    public LabWork updateLab(Long id, LabWorkRequest request) {
        LabWork lab = labWorkRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Лабораторная работа не найдена"));

        lab.setTitle(request.getTitle());
        lab.setComplexity(request.getComplexity());
        lab.setDeadline(request.getDeadline());

        if (request.getSubjectId() != null) {
            lab.setSubject(subjectRepository.findById(request.getSubjectId())
                    .orElse(lab.getSubject()));
        }

        return labWorkRepository.save(lab);
    }

    @Transactional
    public void deleteLab(Long id) {
        labWorkRepository.deleteById(id);
    }

    @Transactional
    public LabWork toggleStatus(Long labId, Long userId) {
        LabWork lab = labWorkRepository.findByLabIdAndUserUserId(labId, userId)
                .orElseThrow(() -> new RuntimeException("Лабораторная работа не найдена"));

        LabStatus nextStatus = switch (lab.getCurrentStatus()) {
            case RECEIVED -> LabStatus.CODED;
            case CODED -> LabStatus.READY;
            case READY -> LabStatus.SUBMITTED;
            case SUBMITTED -> LabStatus.PROTECTED;
            case PROTECTED -> LabStatus.RECEIVED;
        };

        lab.setCurrentStatus(nextStatus);
        return labWorkRepository.save(lab);
    }

    @Transactional
    public void broadcastLabToGroup(LabWorkRequest request, String groupNumber) {
        List<User> students = userRepository.findAllByGroupNumber(groupNumber);
        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Предмет не найден"));

        List<LabWork> labsToSave = students.stream().map(student -> {
            LabWork lab = new LabWork();
            lab.setTitle(request.getTitle());
            lab.setComplexity(request.getComplexity());
            lab.setDeadline(request.getDeadline());
            lab.setSubject(subject);
            lab.setUser(student);
            lab.setCurrentStatus(LabStatus.RECEIVED);
            return lab;
        }).collect(Collectors.toList());

        labWorkRepository.saveAll(labsToSave);
    }

    // ==========================================
    // СТАТИСТИКА ФРОНТЕНДА
    // ==========================================
    public Map<String, Object> getStatistics(Long userId) {
        List<LabWork> labs = labWorkRepository.findAllByUserUserId(userId);

        // 1. Статусы
        List<Map<String, Object>> statusData = labs.stream()
                .collect(Collectors.groupingBy(
                        l -> translateStatus(l.getCurrentStatus()),
                        Collectors.counting()
                )).entrySet().stream()
                .map(e -> Map.<String, Object>of("name", e.getKey(), "value", e.getValue()))
                .collect(Collectors.toList());

        // 2. Предметы
        List<Map<String, Object>> subjectData = labs.stream()
                .filter(l -> l.getSubject() != null)
                .collect(Collectors.groupingBy(
                        l -> l.getSubject().getTitle(),
                        Collectors.counting()
                )).entrySet().stream()
                .map(e -> Map.<String, Object>of("name", e.getKey(), "value", e.getValue()))
                .collect(Collectors.toList());

        // 3. Сложность
        List<Map<String, Object>> complexityData = labs.stream()
                .filter(l -> l.getComplexity() != null)
                .collect(Collectors.groupingBy(
                        LabWork::getComplexity,
                        Collectors.counting()
                )).entrySet().stream()
                .map(e -> Map.<String, Object>of("сложность", "Ур. " + e.getKey(), "количество", e.getValue()))
                .collect(Collectors.toList());

        // 4. Прогресс
        long total = labs.size();
        long completed = labs.stream().filter(l -> l.getCurrentStatus() == LabStatus.PROTECTED).count();
        long inProgress = total - completed;

        List<Map<String, Object>> progressData = List.of(
                Map.<String, Object>of("name", "Защищено", "value", completed),
                Map.<String, Object>of("name", "В работе", "value", inProgress)
        );

        // 5. НОВОЕ: Форма отчетности (Экзамен / Зачет)
        List<Map<String, Object>> controlTypeData = labs.stream()
                .filter(l -> l.getSubject() != null && l.getSubject().getControlType() != null)
                .collect(Collectors.groupingBy(
                        l -> translateControlType(l.getSubject().getControlType()),
                        Collectors.counting()
                )).entrySet().stream()
                .map(e -> Map.<String, Object>of("name", e.getKey(), "value", e.getValue()))
                .collect(Collectors.toList());

        // 6. НОВОЕ: Дедлайны по месяцам
        List<Map<String, Object>> deadlineData = labs.stream()
                .filter(l -> l.getDeadline() != null)
                .collect(Collectors.groupingBy(
                        l -> l.getDeadline().getMonthValue(),
                        Collectors.counting()
                )).entrySet().stream()
                .sorted(Map.Entry.comparingByKey()) // Сортируем месяцы по порядку
                .map(e -> Map.<String, Object>of("month", translateMonth(e.getKey()), "count", e.getValue()))
                .collect(Collectors.toList());

        // Собираем всё в один большой словарь
        Map<String, Object> result = new HashMap<>();
        result.put("statusData", statusData);
        result.put("subjectData", subjectData);
        result.put("complexityData", complexityData);
        result.put("progressData", progressData);
        result.put("totalLabs", total);
        result.put("controlTypeData", controlTypeData); // НОВОЕ
        result.put("deadlineData", deadlineData);       // НОВОЕ

        return result;
    }

    private String translateStatus(LabStatus status) {
        return switch (status) {
            case RECEIVED -> "Получено";
            case CODED -> "Код написан";
            case READY -> "Готово к сдаче";
            case SUBMITTED -> "На проверке";
            case PROTECTED -> "Защищено";
        };
    }

    private String translateControlType(String type) {
        if (type == null) return "Не указано";
        String t = type.toUpperCase();
        if (t.contains("EXAM") || t.contains("ЭКЗАМЕН")) return "Экзамен";
        if (t.contains("CREDIT") || t.contains("ЗАЧЕТ")) return "Зачет";
        return type;
    }

    private String translateMonth(int month) {
        return switch (month) {
            case 1 -> "Янв"; case 2 -> "Фев"; case 3 -> "Мар"; case 4 -> "Апр";
            case 5 -> "Май"; case 6 -> "Июн"; case 7 -> "Июл"; case 8 -> "Авг";
            case 9 -> "Сен"; case 10 -> "Окт"; case 11 -> "Ноя"; case 12 -> "Дек";
            default -> "Неизвестно";
        };
    }
}