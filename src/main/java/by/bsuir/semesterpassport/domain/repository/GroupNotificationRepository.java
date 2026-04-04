package by.bsuir.semesterpassport.domain.repository;

import by.bsuir.semesterpassport.domain.model.GroupNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GroupNotificationRepository extends JpaRepository<GroupNotification, Long> {
    // Получаем список уведомлений для конкретной группы, новые сверху
    List<GroupNotification> findByGroupNumberOrderByCreatedAtDesc(String groupNumber);
}