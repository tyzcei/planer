package by.bsuir.semesterpassport.domain.repository;

import by.bsuir.semesterpassport.domain.model.GroupSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupSubjectRepository extends JpaRepository<GroupSubject, Long> {

    // Метод, который пригодится позже: найти все предметы конкретной группы
    List<GroupSubject> findAllByGroupNumber(String groupNumber);

    // Метод для очистки старых предметов перед добавлением новых
    void deleteAllByGroupNumber(String groupNumber);
}