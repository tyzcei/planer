package by.bsuir.semesterpassport.application.service;

import by.bsuir.semesterpassport.application.dto.GroupSubjectRequest;
import by.bsuir.semesterpassport.domain.model.GroupSubject;
import by.bsuir.semesterpassport.domain.model.Subject;
import by.bsuir.semesterpassport.domain.repository.GroupSubjectRepository;
import by.bsuir.semesterpassport.domain.repository.SubjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupManagementService {

    private final GroupSubjectRepository groupSubjectRepository;
    private final SubjectRepository subjectRepository;

    public GroupManagementService(GroupSubjectRepository groupSubjectRepository,
                                  SubjectRepository subjectRepository) {
        this.groupSubjectRepository = groupSubjectRepository;
        this.subjectRepository = subjectRepository;
    }

    // UC-5: Привязка предметов к группе
    @Transactional
    public void assignSubjectsToGroup(GroupSubjectRequest request) {
        // 1. Проверяем, существуют ли переданные предметы в БД
        List<Subject> subjects = subjectRepository.findAllById(request.subjectIds());
        if (subjects.size() != request.subjectIds().size()) {
            throw new RuntimeException("Один или несколько предметов не найдены в базе данных");
        }

        // 2. Удаляем старые привязки (чтобы не было дубликатов, если староста обновляет список)
        groupSubjectRepository.deleteAllByGroupNumber(request.groupNumber());

        // 3. Создаем новые привязки
        List<GroupSubject> newLinks = subjects.stream().map(subject -> {
            GroupSubject link = new GroupSubject();
            link.setGroupNumber(request.groupNumber());
            link.setSubject(subject);
            return link;
        }).collect(Collectors.toList());

        // 4. Сохраняем всё в БД
        groupSubjectRepository.saveAll(newLinks);
    }
}