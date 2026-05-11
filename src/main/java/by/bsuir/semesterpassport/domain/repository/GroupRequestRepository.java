package by.bsuir.semesterpassport.domain.repository;

import by.bsuir.semesterpassport.domain.model.GroupRequest;
import by.bsuir.semesterpassport.domain.model.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRequestRepository extends JpaRepository<GroupRequest, Long> {
    // Найти все ожидающие запросы
    List<GroupRequest> findByStatusOrderByCreatedAtDesc(RequestStatus status);

    // Проверить, есть ли уже активный запрос от этого юзера (чтобы не спамил)
    boolean existsByUserUserIdAndStatus(Long userId, RequestStatus status);
}