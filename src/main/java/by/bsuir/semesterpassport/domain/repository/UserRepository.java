package by.bsuir.semesterpassport.domain.repository;

import by.bsuir.semesterpassport.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Поиск пользователя по email для аутентификации (UC-1, UC-6)
    // Возвращаем Optional согласно требованиям ТЗ
    Optional<User> findByEmail(String email);

    // Проверка существования пользователя (полезно для валидации регистрации)
    boolean existsByEmail(String email);
}