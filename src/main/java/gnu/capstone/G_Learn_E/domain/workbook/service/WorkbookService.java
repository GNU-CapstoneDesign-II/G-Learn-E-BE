package gnu.capstone.G_Learn_E.domain.workbook.service;

import gnu.capstone.G_Learn_E.domain.folder.entity.Folder;
import gnu.capstone.G_Learn_E.domain.folder.entity.FolderWorkbookMap;
import gnu.capstone.G_Learn_E.domain.folder.repository.FolderRepository;
import gnu.capstone.G_Learn_E.domain.folder.repository.FolderWorkbookMapRepository;
import gnu.capstone.G_Learn_E.domain.problem.converter.ProblemConverter;
import gnu.capstone.G_Learn_E.domain.problem.entity.Problem;
import gnu.capstone.G_Learn_E.domain.problem.repository.ProblemRepository;
import gnu.capstone.G_Learn_E.domain.public_folder.entity.*;
import gnu.capstone.G_Learn_E.domain.public_folder.repository.SubjectRepository;
import gnu.capstone.G_Learn_E.domain.public_folder.repository.SubjectWorkbookMapRepository;
import gnu.capstone.G_Learn_E.domain.user.entity.User;
import gnu.capstone.G_Learn_E.domain.workbook.dto.response.ProblemGenerateResponse;
import gnu.capstone.G_Learn_E.domain.workbook.entity.Workbook;
import gnu.capstone.G_Learn_E.domain.workbook.enums.ExamType;
import gnu.capstone.G_Learn_E.domain.workbook.enums.Semester;
import gnu.capstone.G_Learn_E.domain.workbook.repository.WorkbookRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkbookService {

    private final ProblemRepository problemRepository;
    private final SubjectRepository subjectRepository;
    private final SubjectWorkbookMapRepository subjectWorkbookMapRepository;
    private final WorkbookRepository workbookRepository;
    private final FolderRepository folderRepository;
    private final FolderWorkbookMapRepository folderWorkbookMapRepository;


    public Workbook findWorkbookById(Long workbookId) {
        return workbookRepository.findById(workbookId)
                .orElseThrow(() -> new RuntimeException("Workbook not found"));
    }

    public Workbook findWorkbookByIdWithProblems(Long workbookId) {
        return workbookRepository.findWithMappingsAndProblemsById(workbookId)
                .orElseThrow(() -> new RuntimeException("Workbook not found"));
    }

    public Workbook findUsersWorkbookByIdWithProblems(Long workbookId, User user) {
        Workbook workbook = workbookRepository.findWithMappingsAndProblemsById(workbookId)
                .orElseThrow(() -> new RuntimeException("Workbook not found"));
        if (!workbook.getAuthor().getId().equals(user.getId())) {
            throw new RuntimeException("문제집 소유자가 아닙니다.");
        }
        return workbook;
    }


    public Workbook createWorkbook(ProblemGenerateResponse response, User user){

        Folder rootFolder = folderRepository.findByUserAndParentIsNull(user)
                .orElseThrow(() -> new RuntimeException("기본 폴더가 없습니다."));

        Workbook workbook = Workbook.builder()
                .name(LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .professor("교수명")
                .examType(ExamType.OTHER)
                .coverImage(1)
                .courseYear(LocalDateTime.now().getYear())
                .semester(Semester.OTHER)
                .author(user)
                .build();

        List<Problem> problems = ProblemConverter.convertToProblems(response);
        problemRepository.saveAll(problems);

        int num = 1;
        for(Problem problem : problems) {
            workbook.addProblem(problem, num++);
        }
        workbook = workbookRepository.save(workbook);



        FolderWorkbookMap folderWorkbookMap = FolderWorkbookMap.builder()
                .folder(rootFolder)
                .workbook(workbook)
                .build();
        folderWorkbookMapRepository.save(folderWorkbookMap);

        return workbook;
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


    @Transactional
    public Workbook uploadWorkbook(Long originId, Subject subject, User user) {

        Workbook origin = findUsersWorkbookByIdWithProblems(originId, user);
        // 트랜잭션 관리를 위해 로딩...; 공부해야될듯
        subject = subjectRepository.findById(subject.getId())
                .orElseThrow(() -> new EntityNotFoundException("Subject not found"));

        // 2️⃣ 원본 워크북은 업로드 처리만 표시
        origin.setUploaded(true);   // Dirty-checking으로 자동 update

        // 3️⃣ 메타 정보 복제
        Workbook uploadWorkbook = Workbook.builder()
                .name(origin.getName())
                .professor(origin.getProfessor())
                .examType(origin.getExamType())
                .coverImage(origin.getCoverImage())
                .courseYear(origin.getCourseYear())
                .semester(origin.getSemester())
                .author(origin.getAuthor())
                .build();
        uploadWorkbook.setUploaded(true);
        log.info("업로드할 문제집 : {}", uploadWorkbook);

        // 4️⃣ 같은 Problem 들을 동일 순서로 매핑
        origin.getProblemWorkbookMaps().forEach(map ->
                uploadWorkbook.addProblem(map.getProblem(), map.getProblemNumber())
        );
        log.info("문제 매핑 완료");

        // 5️⃣ Public 영역(Subject)과 매핑
        SubjectWorkbookMap subjectWorkbookMap = SubjectWorkbookMap.builder()
                .id(new SubjectWorkbookId(subject.getId(), uploadWorkbook.getId()))
                .workbook(uploadWorkbook)
                .subject(subject)
                .build();
        subjectWorkbookMapRepository.save(subjectWorkbookMap);
        uploadWorkbook.getSubjectWorkbookMaps().add(subjectWorkbookMap);

        // 6️⃣ 저장 — cascade = ALL 덕분에 매핑 엔티티까지 함께 persist
        return workbookRepository.save(uploadWorkbook);
    }

    @Transactional
    public Workbook downloadWorkbook(Long originId, User user) {
        // 문제집, 문제 리스트
        Workbook origin = findWorkbookByIdWithProblems(originId);
        // 트랜잭션 관리를 위해 로딩...; 공부해야될듯
        if(!subjectWorkbookMapRepository.existsByWorkbook_Id(origin.getId())){
            throw new RuntimeException("공개 문제집이 아닙니다.");
        }

        // 메타 정보 복제
        Workbook downloadWorkbook = Workbook.builder()
                .name(origin.getName())
                .professor(origin.getProfessor())
                .examType(origin.getExamType())
                .coverImage(origin.getCoverImage())
                .courseYear(origin.getCourseYear())
                .semester(origin.getSemester())
                .author(origin.getAuthor())
                .build();
        downloadWorkbook.setUploaded(true);

        // 같은 Problem 들을 동일 순서로 매핑
        origin.getProblemWorkbookMaps().forEach(map ->
                downloadWorkbook.addProblem(map.getProblem(), map.getProblemNumber())
        );
        log.info("문제 매핑 완료");
        Workbook saved = workbookRepository.save(downloadWorkbook);

        // 유저의 루트 폴더에 다운로드 문제집을 매핑
        Folder rootFolder = folderRepository.findByUserAndParentIsNull(user)
                .orElseThrow(() -> new RuntimeException("기본 폴더가 없습니다."));
        FolderWorkbookMap folderWorkbookMap = FolderWorkbookMap.builder()
                .folder(rootFolder)
                .workbook(saved)
                .build();

        saved.getFolderWorkbookMaps().add(folderWorkbookMap);

        // 6️⃣ 저장 — cascade = ALL 덕분에 매핑 엔티티까지 함께 persist
        return saved;
    }
}
