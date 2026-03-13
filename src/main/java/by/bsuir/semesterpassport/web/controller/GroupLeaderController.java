package by.bsuir.semesterpassport.web.controller;

import by.bsuir.semesterpassport.application.dto.GroupSubjectRequest;
import by.bsuir.semesterpassport.application.service.GroupManagementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/group-management")
@PreAuthorize("hasAuthority('GROUP_LEADER')") // Пускаем только старост
public class GroupLeaderController {

    private final GroupManagementService groupManagementService;

    public GroupLeaderController(GroupManagementService groupManagementService) {
        this.groupManagementService = groupManagementService;
    }

    // UC-5: Формирование списка предметов
    @PostMapping("/subjects")
    public ResponseEntity<String> assignSubjects(@RequestBody GroupSubjectRequest request) {
        // Вызываем реальную бизнес-логику
        groupManagementService.assignSubjectsToGroup(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Предметы успешно привязаны к группе " + request.groupNumber());
    }
}