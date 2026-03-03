package by.bsuir.semesterpassport.application.service;

import by.bsuir.semesterpassport.domain.model.LabWork;
import org.springframework.stereotype.Service;
import java.io.ByteArrayInputStream;
import java.util.List;

@Service
public class PdfReportService {

    // В учебном проекте мы можем просто сформировать структуру данных для PDF
    public byte[] generateDebtReport(List<LabWork> debts) {
        // Здесь будет логика формирования PDF документа
        // На опроцентовке можно показать, что метод вызывается и возвращает массив байтов
        return "PDF Content".getBytes();
    }
}