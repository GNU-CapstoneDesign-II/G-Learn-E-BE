package gnu.capstone.G_Learn_E.domain.workbook.service;

import gnu.capstone.G_Learn_E.domain.workbook.converter.WorkbookConverter;
import gnu.capstone.G_Learn_E.domain.workbook.dto.response.ProblemGenerateResponse;
import gnu.capstone.G_Learn_E.domain.workbook.entity.Workbook;
import gnu.capstone.G_Learn_E.domain.workbook.enums.ExamType;
import gnu.capstone.G_Learn_E.domain.workbook.enums.Semester;
import gnu.capstone.G_Learn_E.domain.workbook.repository.WorkbookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkbookService {

    private final WorkbookRepository workbookRepository;


    public Workbook createWorkbook(ProblemGenerateResponse response){
        Workbook workbookTemplate = Workbook.builder()
                .name(LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .professor("교수명")
                .examType(ExamType.OTHER)
                .coverImage(1)
                .courseYear(LocalDateTime.now().getYear())
                .semester(Semester.OTHER)
                .build();

        Workbook newWorkbook = WorkbookConverter.convertToWorkbookAndProblems(response, workbookTemplate);
        return workbookRepository.save(newWorkbook);
    }
}
