package gnu.capstone.G_Learn_E.domain.workbook.service;

import gnu.capstone.G_Learn_E.domain.folder.entity.Folder;
import gnu.capstone.G_Learn_E.domain.folder.entity.FolderWorkbookId;
import gnu.capstone.G_Learn_E.domain.folder.entity.FolderWorkbookMap;
import gnu.capstone.G_Learn_E.domain.folder.repository.FolderRepository;
import gnu.capstone.G_Learn_E.domain.folder.repository.FolderWorkbookMapRepository;
import gnu.capstone.G_Learn_E.domain.user.entity.User;
import gnu.capstone.G_Learn_E.domain.workbook.converter.WorkbookConverter;
import gnu.capstone.G_Learn_E.domain.workbook.dto.response.ProblemGenerateResponse;
import gnu.capstone.G_Learn_E.domain.workbook.entity.Workbook;
import gnu.capstone.G_Learn_E.domain.workbook.enums.ExamType;
import gnu.capstone.G_Learn_E.domain.workbook.enums.Semester;
import gnu.capstone.G_Learn_E.domain.workbook.repository.WorkbookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkbookService {

    private final WorkbookRepository workbookRepository;
    private final FolderRepository folderRepository;
    private final FolderWorkbookMapRepository folderWorkbookMapRepository;


    public Workbook createWorkbook(ProblemGenerateResponse response, User user){

        Folder rootFolder = folderRepository.findByUserAndParentIsNull(user)
                .orElseThrow(() -> new RuntimeException("기본 폴더가 없습니다."));

        Workbook workbookTemplate = Workbook.builder()
                .name(LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .professor("교수명")
                .examType(ExamType.OTHER)
                .coverImage(1)
                .courseYear(LocalDateTime.now().getYear())
                .semester(Semester.OTHER)
                .build();

        Workbook newWorkbook = WorkbookConverter.convertToWorkbookAndProblems(response, workbookTemplate);
        newWorkbook = workbookRepository.save(newWorkbook);


        FolderWorkbookMap folderWorkbookMap = FolderWorkbookMap.builder()
                .folder(rootFolder)
                .workbook(newWorkbook)
                .build();
        folderWorkbookMapRepository.save(folderWorkbookMap);

        return newWorkbook;
    }


    public List<Workbook> getChildrenWorkbooks(Folder folder) {
        if (Hibernate.isInitialized(folder.getFolderWorkbookMaps())) {
            // folder workbook이 초기화 된 경우
            return folder.getFolderWorkbookMaps().stream()
                    .map(FolderWorkbookMap::getWorkbook)
                    .toList();
        } else {
            // folder workbook이 초기화 되지 않은 경우
            return folderWorkbookMapRepository.findByFolderWithWorkbook(folder)
                    .stream()
                    .map(FolderWorkbookMap::getWorkbook)
                    .toList();
        }
    }
}
