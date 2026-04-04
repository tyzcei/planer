package by.bsuir.semesterpassport.application.service;

import by.bsuir.semesterpassport.domain.model.CourseTeacher;
import by.bsuir.semesterpassport.domain.model.Subject; // НОВЫЙ ИМПОРТ
import by.bsuir.semesterpassport.domain.repository.CourseTeacherRepository;
import by.bsuir.semesterpassport.domain.repository.SubjectRepository; // НОВЫЙ ИМПОРТ
import by.bsuir.semesterpassport.domain.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TeacherSyncService {

    private final CourseTeacherRepository courseTeacherRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository; // НОВОЕ ПОЛЕ
    private final RestTemplate restTemplate;

    // ОБНОВЛЕННЫЙ КОНСТРУКТОР
    public TeacherSyncService(CourseTeacherRepository courseTeacherRepository,
                              UserRepository userRepository,
                              SubjectRepository subjectRepository) {
        this.courseTeacherRepository = courseTeacherRepository;
        this.userRepository = userRepository;
        this.subjectRepository = subjectRepository;
        this.restTemplate = createTrustAllRestTemplate();
    }

    private RestTemplate createTrustAllRestTemplate() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                    }
            };
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
            return new RestTemplate();
        } catch (Exception e) {
            e.printStackTrace();
            return new RestTemplate();
        }
    }

    @Scheduled(cron = "0 0 3 * * SUN")
    @Transactional
    public void syncTeachersWeekly() {
        System.out.println("⏳ Запуск еженедельной синхронизации преподавателей...");
        Set<String> allGroups = userRepository.findAll().stream()
                .map(user -> user.getGroupNumber())
                .filter(group -> group != null && !group.isEmpty())
                .collect(Collectors.toSet());

        for (String groupNumber : allGroups) {
            syncTeachersForGroup(groupNumber);
        }
        System.out.println("✅ Синхронизация преподавателей завершена!");
    }



    @Transactional
    public void syncTeachersForGroup(String groupNumber) {
        try {
            String apiUrl = "https://iis.bsuir.by/api/v1/schedule?studentGroup=" + groupNumber;
            Map<String, Object> response = restTemplate.getForObject(apiUrl, Map.class);

            if (response == null || !response.containsKey("schedules")) return;

            Map<String, List<Map<String, Object>>> schedules = (Map<String, List<Map<String, Object>>>) response.get("schedules");

            courseTeacherRepository.deleteByGroupNumber(groupNumber);

            // Кэш для предметов, чтобы не дергать базу данных на каждой итерации цикла
            Set<String> processedSubjects = new HashSet<>();

            for (List<Map<String, Object>> dayLessons : schedules.values()) {
                for (Map<String, Object> lesson : dayLessons) {

                    String subjectTitle = (String) lesson.get("subject");
                    String lessonType = (String) lesson.get("lessonTypeAbbrev");
                    List<Map<String, String>> employees = (List<Map<String, String>>) lesson.get("employees");

                    // === 1. УМНОЕ ПОПОЛНЕНИЕ ТАБЛИЦЫ ПРЕДМЕТОВ ===
                    // Внутри цикла по занятиям в TeacherSyncService.java
                    if (subjectTitle != null && !processedSubjects.contains(subjectTitle)) {
                        // Проверяем существование ПРЕДМЕТА именно для этой ГРУППЫ
                        boolean exists = subjectRepository.existsByTitleAndGroupNumber(subjectTitle, groupNumber);

                        if (!exists) {
                            Subject newSubject = new Subject();
                            newSubject.setTitle(subjectTitle);
                            newSubject.setGroupNumber(groupNumber); // ПРИВЯЗЫВАЕМ К ГРУППЕ
                            newSubject.setControlType("CREDIT");
                            subjectRepository.save(newSubject);
                            System.out.println("📚 Предмет [" + subjectTitle + "] привязан к группе " + groupNumber);
                        }
                        processedSubjects.add(subjectTitle);
                    }

                    // === 2. ОБНОВЛЕНИЕ ПРЕПОДАВАТЕЛЕЙ ===
                    if (employees != null && !employees.isEmpty() && subjectTitle != null) {
                        for (Map<String, String> emp : employees) {
                            String urlId = emp.get("urlId");
                            String lastName = emp.get("lastName");
                            String firstName = emp.get("firstName");
                            String middleName = emp.get("middleName");
                            String fullName = lastName + " " + (firstName != null ? firstName.charAt(0) + "." : "") + (middleName != null ? middleName.charAt(0) + "." : "");
                            String photoLink = emp.get("photoLink");

                            CourseTeacher teacher = new CourseTeacher(
                                    groupNumber, subjectTitle, fullName, urlId, lessonType, photoLink
                            );

                            if (!courseTeacherRepository.existsByGroupNumberAndSubjectTitleAndBsuirUrlId(groupNumber, subjectTitle, urlId)) {
                                courseTeacherRepository.save(teacher);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка синхронизации группы " + groupNumber + ": " + e.getMessage());
        }
    }
}