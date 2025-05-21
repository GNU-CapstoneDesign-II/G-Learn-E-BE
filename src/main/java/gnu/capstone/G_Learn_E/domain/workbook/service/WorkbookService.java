package gnu.capstone.G_Learn_E.domain.workbook.service;

import gnu.capstone.G_Learn_E.domain.folder.entity.Folder;
import gnu.capstone.G_Learn_E.domain.folder.entity.FolderWorkbookMap;
import gnu.capstone.G_Learn_E.domain.folder.repository.FolderRepository;
import gnu.capstone.G_Learn_E.domain.folder.repository.FolderWorkbookMapRepository;
import gnu.capstone.G_Learn_E.domain.problem.converter.ProblemConverter;
import gnu.capstone.G_Learn_E.domain.problem.dto.request.ProblemRequest;
import gnu.capstone.G_Learn_E.domain.problem.entity.Problem;
import gnu.capstone.G_Learn_E.domain.problem.entity.ProblemWorkbookMap;
import gnu.capstone.G_Learn_E.domain.problem.enums.ProblemType;
import gnu.capstone.G_Learn_E.domain.problem.repository.ProblemRepository;
import gnu.capstone.G_Learn_E.domain.problem.repository.ProblemWorkbookMapRepository;
import gnu.capstone.G_Learn_E.domain.public_folder.entity.*;
import gnu.capstone.G_Learn_E.domain.public_folder.repository.SubjectRepository;
import gnu.capstone.G_Learn_E.domain.public_folder.repository.SubjectWorkbookMapRepository;
import gnu.capstone.G_Learn_E.domain.user.entity.User;
import gnu.capstone.G_Learn_E.domain.workbook.dto.request.WorkbookUpdateRequest;
import gnu.capstone.G_Learn_E.domain.workbook.dto.response.ProblemGenerateResponse;
import gnu.capstone.G_Learn_E.domain.workbook.dto.response.WorkbookSimpleResponse;
import gnu.capstone.G_Learn_E.domain.workbook.entity.DownloadedWorkbookMap;
import gnu.capstone.G_Learn_E.domain.workbook.entity.Workbook;
import gnu.capstone.G_Learn_E.domain.workbook.enums.ExamType;
import gnu.capstone.G_Learn_E.domain.workbook.enums.Semester;
import gnu.capstone.G_Learn_E.domain.workbook.repository.DownloadedWorkbookMapRepository;
import gnu.capstone.G_Learn_E.domain.workbook.repository.WorkbookRepository;
import gnu.capstone.G_Learn_E.global.common.serialization.Option;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkbookService {

    private final ProblemRepository problemRepository;
    private final ProblemWorkbookMapRepository problemWorkbookMapRepository;
    private final SubjectRepository subjectRepository;
    private final SubjectWorkbookMapRepository subjectWorkbookMapRepository;
    private final WorkbookRepository workbookRepository;
    private final FolderRepository folderRepository;
    private final FolderWorkbookMapRepository folderWorkbookMapRepository;
    private final DownloadedWorkbookMapRepository downloadedWorkbookMapRepository;

    private static final Normalizer.Form NF = Normalizer.Form.NFC;


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

    @Transactional
    public Workbook createWorkbookFromProblems(String workbookTitle, List<ProblemRequest> problemRequests, User user) {
        Folder rootFolder = folderRepository.findByUserAndParentIsNull(user)
                .orElseThrow(() -> new RuntimeException("기본 폴더가 없습니다."));

        Workbook workbook = Workbook.builder()
                .name(workbookTitle)
                .professor("교수명")
                .examType(ExamType.OTHER)
                .coverImage(1)
                .courseYear(LocalDateTime.now().getYear())
                .semester(Semester.OTHER)
                .author(user)
                .build();

        Workbook savedWorkbook = workbookRepository.save(workbook);

        // Problem 객체 찾아오기
        Map<Long, Problem> problems = problemRepository.findAllById(
                problemRequests.stream()
                        .map(ProblemRequest::id)
                        .toList()
        ).stream().collect(Collectors.toMap(Problem::getId, problem -> problem));

        int problemNumber = 1;
        for(ProblemRequest problemRequest : problemRequests) {
            if(isEqualProblem(problems.get(problemRequest.id()), problemRequest)) {
                // 문제를 찾았고, 같으면 문제집에 추가
                savedWorkbook.addProblem(problems.get(problemRequest.id()), problemNumber++);
            } else {
                // 다르면 문제 엔티티 새로 생성해서 문제집에 추가
                Problem problem = ProblemConverter.convertToProblem(problemRequest);
                problemRepository.save(problem);
                savedWorkbook.addProblem(problem, problemNumber++);
            }
        }

        FolderWorkbookMap folderWorkbookMap = FolderWorkbookMap.builder()
                .folder(rootFolder)
                .workbook(workbook)
                .build();
        savedWorkbook.getFolderWorkbookMaps().add(folderWorkbookMap);
        return savedWorkbook;
    }


    @Transactional
    public Workbook replaceProblemsInWorkbook(
            Long workbookId,
            List<ProblemRequest> problemRequests,
            User user
    ) {
        // 🔹 (a) 매핑 테이블 싹 제거 – 벌크 연산
        problemWorkbookMapRepository.deleteByWorkbookId(workbookId);
        // => delete 쿼리 즉시 실행 + 영속성 컨텍스트 동기화(clearAutomatically)

        Workbook workbook = workbookRepository.findById(workbookId)
                .orElseThrow(() -> new RuntimeException("Workbook not found"));

        // 🔹 (b) 문제 엔티티 캐싱
        Map<Long, Problem> existing = problemRepository.findAllById(
                problemRequests.stream().map(ProblemRequest::id).toList()
        ).stream().collect(Collectors.toMap(Problem::getId, p -> p));

        // 🔹 (c) 다시 매핑
        int seq = 1;
        boolean changed = false;
        List<Problem> newProblems = new ArrayList<>();
        for (ProblemRequest req : problemRequests) {
            Problem p = existing.get(req.id());
            if (p != null && isEqualProblem(p, req)) {
                workbook.addProblem(p, seq++);
            } else {
                Problem newP = ProblemConverter.convertToProblem(req);
                newProblems.add(newP);
                workbook.addProblem(newP, seq++);
                changed = true;
            }
        }
        if (!newProblems.isEmpty()) {
            problemRepository.saveAll(newProblems);
        }
        if (changed){
            workbook.setAuthor(user);
            workbook.setUploaded(false);
        }

        return workbook;      // flush 는 트랜잭션 끝에서
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
        if(origin.isUploaded()) {
            throw new RuntimeException("이미 업로드된 문제집입니다.");
        }
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

        workbookRepository.save(uploadWorkbook);      // persist
        workbookRepository.flush();

        // 5️⃣ Public 영역(Subject)과 매핑
        SubjectWorkbookMap subjectWorkbookMap = SubjectWorkbookMap.builder()
                .id(new SubjectWorkbookId(subject.getId(), uploadWorkbook.getId()))
                .workbook(uploadWorkbook)
                .subject(subject)
                .build();
        subjectWorkbookMapRepository.save(subjectWorkbookMap);

        // 6️⃣ 저장 — cascade = ALL 덕분에 매핑 엔티티까지 함께 persist
        return uploadWorkbook;
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

        DownloadedWorkbookMap downloadedWorkbookMap = DownloadedWorkbookMap.builder()
                .user(user)
                .workbook(origin)
                .build();
        downloadedWorkbookMapRepository.save(downloadedWorkbookMap);

        // 6️⃣ 저장 — cascade = ALL 덕분에 매핑 엔티티까지 함께 persist
        return saved;
    }

    @Transactional
    public Workbook renameWorkbook(Long workbookId, String newName) {
        Workbook workbook = workbookRepository.findById(workbookId)
                .orElseThrow(() -> new RuntimeException("Workbook not found"));
        workbook.setName(newName);
        return workbookRepository.save(workbook);
    }

    public Workbook updateWorkbook(Long workbookId, WorkbookUpdateRequest request) {
        ExamType examType = ExamType.valueOf(request.examType());
        Semester semester = Semester.valueOf(request.semester());

        Workbook workbook = findWorkbookById(workbookId);
        workbook.updateWorkbook(
                request.name(),
                request.professor(),
                examType,
                workbook.getCoverImage(),
                request.courseYear(),
                semester
        );
        return workbookRepository.save(workbook);
    }

    public List<WorkbookSimpleResponse> getWorkbooksByRelativeKeyword(
            User user,
            String keyword,
            int page, int size
    ) {
        List<Workbook> workbooks = workbookRepository.findAccessibleByKeyword(keyword, user.getId());

        Map<Workbook, Long> countMap = workbooks.stream()
                .collect(Collectors.toMap(
                        w -> w,
                        w -> w.getProblemWorkbookMaps().stream()
                                .map(ProblemWorkbookMap::getProblem)
                                .filter(p -> p.getProblemKeywords().stream()
                                        .anyMatch(pk -> pk.getKeyword().equals(keyword)))
                                .distinct()
                                .count()
                ));

        // 3) 개수 내림차순 정렬 후 DTO 리스트로 변환
        return countMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .skip((long) page * size)
                // size 개수만큼 제한
                .limit(size)
                .map(e -> {
                    Workbook w = e.getKey();
                    return WorkbookSimpleResponse.from(w);
                })
                .toList();
    }




    private boolean isEqualProblem(Problem p, ProblemRequest r) {

        // 1️⃣ Enum
        if (p.getType() != ProblemType.valueOf(r.type())) return false;

        // 2️⃣ 제목 (한글 정규화)
        if (!Objects.equals(norm(p.getTitle()), norm(r.title()))) return false;

        // 3️⃣ 선택지 비교 — null ↔ 빈 리스트 허용
        if (!equalsList(toContents(p.getOptions()), r.options())) return false;

        // 4️⃣ 정답 비교 (동일)
        if (!equalsList(p.getAnswers(), r.answers())) return false;

        // 5️⃣ 해설 — null/빈/공백 동등 처리
        return Objects.equals(normalizeBlank(p.getExplanation()),
                normalizeBlank(r.explanation()));
    }

    /* ---------- helper ---------- */
    private static String norm(String s) {
        return s == null ? null : Normalizer.normalize(s, NF);
    }

    private static List<String> toContents(List<Option> opts) {
        return opts == null ? List.of() :
                opts.stream().map(Option::getContent).toList();
    }

    private static <T> boolean equalsList(List<T> a, List<T> b) {
        if ((a == null || a.isEmpty()) && (b == null || b.isEmpty())) return true;
        return Objects.equals(a, b);
    }

    private static String normalizeBlank(String s) {
        return (s == null || s.isBlank()) ? null : norm(s);
    }
}
