package by.bsuir.semesterpassport.domain.repository;

import by.bsuir.semesterpassport.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findAllByGroupNumber(String groupNumber);

    // НОВЫЙ МЕТОД: Получает список уникальных номеров групп из базы
    @Query("SELECT DISTINCT u.groupNumber FROM User u WHERE u.groupNumber IS NOT NULL AND u.groupNumber <> ''")
    List<String> findDistinctGroupNumbers();
}