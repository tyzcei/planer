package by.bsuir.semesterpassport.application.service;

import by.bsuir.semesterpassport.domain.model.CourseTeacher;
import by.bsuir.semesterpassport.domain.model.Subject;
import by.bsuir.semesterpassport.domain.model.Teacher;
import by.bsuir.semesterpassport.domain.repository.CourseTeacherRepository;
import by.bsuir.semesterpassport.domain.repository.SubjectRepository;
import by.bsuir.semesterpassport.domain.repository.TeacherRepository;
import by.bsuir.semesterpassport.domain.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
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
    private final SubjectRepository subjectRepository;
    private final TeacherRepository teacherRepository;
    private final RestTemplate restTemplate;

    public TeacherSyncService(CourseTeacherRepository courseTeacherRepository,
                              UserRepository userRepository,
                              SubjectRepository subjectRepository,
                              TeacherRepository teacherRepository) {
        this.courseTeacherRepository = courseTeacherRepository;
        this.userRepository = userRepository;
        this.subjectRepository = subjectRepository;
        this.teacherRepository = teacherRepository;
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
        Set<String> allGroups = userRepository.findAll().stream()
                .map(user -> user.getGroupNumber())
                .filter(group -> group != null && !group.isEmpty())
                .collect(Collectors.toSet());

        for (String groupNumber : allGroups) {
            try {
                syncTeachersForGroup(groupNumber);
            } catch (Exception e) {
                System.err.println("Не удалось синхронизировать группу: " + groupNumber);
            }
        }
    }

    @Transactional
    @CacheEvict(value = "scheduleCache", key = "#groupNumber")
    public void syncTeachersForGroup(String groupNumber) {
        try {
            String apiUrl = "https://iis.bsuir.by/api/v1/schedule?studentGroup=" + groupNumber;
            Map<String, Object> response = restTemplate.getForObject(apiUrl, Map.class);

            if (response == null || !response.containsKey("schedules")) return;

            Map<String, List<Map<String, Object>>> schedules = (Map<String, List<Map<String, Object>>>) response.get("schedules");

            // Очищаем старые связки расписания (сами преподаватели и предметы остаются в базе)
            courseTeacherRepository.deleteByGroupNumber(groupNumber);

            Set<String> processedTeachersInCycle = new HashSet<>();

            for (List<Map<String, Object>> dayLessons : schedules.values()) {
                for (Map<String, Object> lesson : dayLessons) {

                    String subjectTitle = (String) lesson.get("subject");
                    String lessonType = (String) lesson.get("lessonTypeAbbrev");

                    Object employeesObj = lesson.get("employees");
                    if (employeesObj instanceof List && subjectTitle != null) {
                        List<Map<String, Object>> employees = (List<Map<String, Object>>) employeesObj;

                        // 1. Ищем или создаем ПРЕДМЕТ
                        Subject subject = subjectRepository.findByTitleAndGroupNumber(subjectTitle, groupNumber)
                                .orElseGet(() -> {
                                    Subject newSubject = new Subject();
                                    newSubject.setTitle(subjectTitle);
                                    newSubject.setGroupNumber(groupNumber);
                                    newSubject.setControlType("CREDIT");
                                    return subjectRepository.save(newSubject);
                                });

                        for (Map<String, Object> emp : employees) {
                            String urlId = emp.get("urlId") != null ? String.valueOf(emp.get("urlId")) : "unknown";
                            String uniqueKey = urlId + "_" + subjectTitle;

                            if (!processedTeachersInCycle.contains(uniqueKey)) {

                                // 2. Ищем или создаем ПРЕПОДАВАТЕЛЯ
                                Teacher teacher = teacherRepository.findByBsuirUrlId(urlId)
                                        .orElseGet(() -> {
                                            String lastName = emp.get("lastName") != null ? String.valueOf(emp.get("lastName")) : "";
                                            String firstName = emp.get("firstName") != null ? String.valueOf(emp.get("firstName")) : "";
                                            String middleName = emp.get("middleName") != null ? String.valueOf(emp.get("middleName")) : "";
                                            String fullName = lastName + " " + (!firstName.isEmpty() ? firstName.charAt(0) + "." : "") + (!middleName.isEmpty() ? middleName.charAt(0) + "." : "");
                                            String photoLink = emp.get("photoLink") != null ? String.valueOf(emp.get("photoLink")) : null;

                                            return teacherRepository.save(new Teacher(urlId, fullName.trim(), photoLink));
                                        });

                                // 3. Создаем СВЯЗКУ (CourseTeacher)
                                if (!courseTeacherRepository.existsByGroupNumberAndSubject_TitleAndTeacher_BsuirUrlId(groupNumber, subjectTitle, urlId)) {
                                    courseTeacherRepository.save(new CourseTeacher(groupNumber, subject, teacher, lessonType));
                                }

                                processedTeachersInCycle.add(uniqueKey);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка парсинга API: " + e.getMessage(), e);
        }
    }
}