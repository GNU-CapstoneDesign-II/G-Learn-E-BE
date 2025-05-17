package gnu.capstone.G_Learn_E.domain.workbook.controller;

import gnu.capstone.G_Learn_E.domain.folder.service.FolderService;
import gnu.capstone.G_Learn_E.domain.problem.entity.Problem;
import gnu.capstone.G_Learn_E.domain.problem.entity.ProblemWorkbookMap;
import gnu.capstone.G_Learn_E.domain.problem.service.ProblemService;
import gnu.capstone.G_Learn_E.domain.public_folder.entity.Subject;
import gnu.capstone.G_Learn_E.domain.public_folder.service.PublicFolderService;
import gnu.capstone.G_Learn_E.domain.solve_log.dto.request.SaveSolveLogRequest;
import gnu.capstone.G_Learn_E.domain.user.entity.UserLevelPolicy;
import gnu.capstone.G_Learn_E.domain.user.service.UserService;
import gnu.capstone.G_Learn_E.domain.workbook.converter.WorkbookConverter;
import gnu.capstone.G_Learn_E.domain.workbook.dto.request.*;
import gnu.capstone.G_Learn_E.domain.workbook.dto.response.*;
import gnu.capstone.G_Learn_E.domain.solve_log.entity.SolveLog;
import gnu.capstone.G_Learn_E.domain.solve_log.entity.SolvedWorkbook;
import gnu.capstone.G_Learn_E.domain.solve_log.service.SolveLogService;
import gnu.capstone.G_Learn_E.domain.user.entity.User;
import gnu.capstone.G_Learn_E.domain.workbook.entity.Workbook;
import gnu.capstone.G_Learn_E.domain.workbook.service.WorkbookService;
import gnu.capstone.G_Learn_E.domain.workbook.service.WorkbookVoteService;
import gnu.capstone.G_Learn_E.global.fastapi.service.FastApiService;
import gnu.capstone.G_Learn_E.global.template.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/workbook")
@Tag(name = "문제집 API")
@RequiredArgsConstructor
public class WorkbookController {

    private final UserService userService;
    private final ProblemService problemService;
    private final WorkbookService workbookService;
    private final FolderService folderService;
    private final PublicFolderService publicFolderService;
    private final WorkbookVoteService workbookVoteService;
    private final SolveLogService solveLogService;
    private final FastApiService fastApiService;


    @GetMapping("/{workbookId}")
    @Operation(summary = "문제집 정보 조회", description = "문제집 정보를 조회합니다.")
    public ApiResponse<?> getWorkbook(
            @AuthenticationPrincipal User user,
            @PathVariable("workbookId") Long workbookId
    ) {
        log.info("문제집 정보 조회 요청 : {}", workbookId);
        if(!folderService.isWorkbookInUserFolder(user, workbookId) &&
                !publicFolderService.isPublicWorkbook(workbookId)) {
            throw new RuntimeException("문제집 접근 권한이 없습니다.");
        }
        Workbook workbook = workbookService.findWorkbookById(workbookId);
        WorkbookSimpleResponse response = WorkbookSimpleResponse.from(workbook);
        return new ApiResponse<>(HttpStatus.OK, "문제집 정보 조회 성공", response);
    }


    @Operation(summary = "문제집 생성", description = "문제집을 생성합니다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, path = "/generate")
    public ApiResponse<WorkbookResponse> createWorkbook(
            @AuthenticationPrincipal User user,
            @ModelAttribute ProblemGenerateRequest request
    ) {
        ProblemGenerateResponse problemGenerateResponse = fastApiService.generateProblems(request);
        log.info("User : {}", user);
        log.info("Response : {}", problemGenerateResponse);

        Workbook workbook = workbookService.createWorkbook(problemGenerateResponse, user);

        WorkbookResponse response = WorkbookResponse.of(workbook);

        // 유저의 문제집 생성 개수 증가
        userService.plusCreateWorkbookCount(user);
        // 문제집 생성 시 경험치 부여
        userService.gainExp(user, UserLevelPolicy.EXP_CREATE_WORKBOOK);
        // Workbook 생성 로직 처리 후 결과 반환
        return new ApiResponse<>(HttpStatus.OK, "문제집 생성에 성공하였습니다.", response);
    }

    @Operation(summary = "문제집 이름 변경", description = "문제집 이름을 변경합니다.")
    @PatchMapping("/{workbookId}/rename")
    public ApiResponse<?> renameWorkbook(
            @AuthenticationPrincipal User user,
            @PathVariable("workbookId") Long workbookId,
            @RequestBody WorkbookRenameRequest request
    ) {
        log.info("문제집 이름 변경 요청 : {}", request);
        if(!folderService.isWorkbookInUserFolder(user, workbookId)) {
            throw new RuntimeException("문제집 접근 권한이 없습니다.");
        }
        Workbook workbook = workbookService.renameWorkbook(workbookId, request.newName());
        WorkbookSimpleResponse response = WorkbookSimpleResponse.from(workbook);
        return new ApiResponse<>(HttpStatus.OK, "문제집 이름 변경 성공", response);
    }

    @Operation(summary = "문제집 정보 수정", description = "문제집 정보를 수정합니다.")
    @PatchMapping("/{workbookId}")
    public ApiResponse<?> updateWorkbook(
            @AuthenticationPrincipal User user,
            @PathVariable("workbookId") Long workbookId,
            @RequestBody WorkbookUpdateRequest request
    ) {
        log.info("문제집 수정 요청 : {}", request);
        if(!folderService.isWorkbookInUserFolder(user, workbookId)) {
            throw new RuntimeException("문제집 접근 권한이 없습니다.");
        }
        Workbook workbook = workbookService.updateWorkbook(workbookId, request);
        WorkbookSimpleResponse response = WorkbookSimpleResponse.from(workbook);
        return new ApiResponse<>(HttpStatus.OK, "문제집 정보 수정 성공", response);
    }

    /**
     * 개인 폴더 문제 풀이 페이지 로드
     */
    @Operation(summary = "문제 풀이 페이지 로드", description = "문제 풀이 페이지를 로드합니다.")
    @GetMapping("/{workbookId}/solve")
    public ApiResponse<WorkbookSolveResponse> problemSolvePageLoad(
            @AuthenticationPrincipal User user,
            @PathVariable("workbookId") Long workbookId
    ){
        Workbook workbook = workbookService.findWorkbookByIdWithProblems(workbookId);
        List<ProblemWorkbookMap> problemWorkbookMaps = workbook.getProblemWorkbookMaps();
        List<Problem> problems = problemWorkbookMaps.stream()
                .map(ProblemWorkbookMap::getProblem)
                .toList();
        log.info("문제집 조회 성공 : {}", workbook);
        if(!folderService.isWorkbookInUserFolder(user, workbookId) &&
                !publicFolderService.isPublicWorkbook(workbookId)) {
            throw new RuntimeException("문제집 접근 권한이 없습니다.");
        }

        SolvedWorkbook solvedWorkbook = solveLogService.findOrCreateSolvedWorkbook(workbook, user);
        log.info("문제집 풀이 기록 조회 성공 : {}", solvedWorkbook);

        List<SolveLog> solveLogs = solveLogService.findOrCreateAllSolveLog(solvedWorkbook, problems);
        Map<Long, SolveLog> solveLogToMap = solveLogs.stream().collect(
                Collectors.toMap(SolveLog::getProblemId, solveLog -> solveLog)
        );

        log.info("문제 풀이 기록 조회 성공 : {}", solveLogToMap);

        WorkbookSolveResponse response = WorkbookConverter.convertToWorkbookSolveResponse(
                workbook,
                solvedWorkbook,
                problemWorkbookMaps,
                solveLogToMap
        );
        log.info("문제 풀이 페이지 로드 성공 : {}", response);
        return new ApiResponse<>(HttpStatus.OK, "문제 풀이 페이지 로드 성공", response);
    }

    @Operation(summary = "문제 풀이 채점", description = "문제 풀이를 채점합니다.")
    @PostMapping("/{workbookId}/grade")
    public ApiResponse<?> gradeWorkbook(
            @AuthenticationPrincipal User user,
            @PathVariable("workbookId") Long workbookId,
            @RequestBody SaveSolveLogRequest request
    ){
        if(!folderService.isWorkbookInUserFolder(user, workbookId) &&
                !publicFolderService.isPublicWorkbook(workbookId)) {
            throw new RuntimeException("문제집 접근 권한이 없습니다.");
        }
        Workbook workbook = workbookService.findWorkbookByIdWithProblems(workbookId);
        GradeWorkbookResponse response = solveLogService.gradeWorkbook(user, workbook, request);

        boolean isNewSolved = solveLogService.updateSolvedWorkbookCountByUser(user);
        if(isNewSolved) {
            userService.gainExp(
                    user,
                    (int) Math.round(
                            UserLevelPolicy.EXP_SOLVE_PROBLEM
                                    * ((double) response.correctCount()
                                    / (response.correctCount() + response.wrongCount()))
                    )
            );
        }
        else {
            userService.gainExp(
                    user,
                    (int) Math.round(
                            UserLevelPolicy.EXP_RESOLVE_PROBLEM
                                    * ((double) response.correctCount()
                                    / (response.correctCount() + response.wrongCount()))
                    )
            );
        }

        return new ApiResponse<>(HttpStatus.OK, "문제 풀이 채점 성공", response);
    }

    @Operation(summary = "문제집 업로드", description = "문제집을 public 폴더에 업로드합니다.")
    @PostMapping("/{workbookId}/upload")
    public ApiResponse<?> uploadWorkbook(
            @AuthenticationPrincipal User user,
            @PathVariable("workbookId") Long workbookId,
            @RequestBody WorkbookUpload request
    ){
        log.info("문제집 업로드 요청 : {}", request);
        Subject subject = publicFolderService.findSubjectInTree(request.collegeId(), request.departmentId(), request.subjectId());
        Workbook workbook = workbookService.uploadWorkbook(workbookId, subject, user);
        WorkbookUploadResponse response = WorkbookUploadResponse.of(
                workbook.getId(),
                subject.getId(),
                subject.getName()
        );
        userService.gainExp(user, UserLevelPolicy.EXP_UPLOAD_WORKBOOK);
        return new ApiResponse<>(HttpStatus.OK, "문제집 업로드 성공", response);
    }

    @Operation(summary = "문제집 리스트 업로드", description = "문제집 리스트를 public 폴더에 업로드합니다. (TODO)")
    @PostMapping("/upload/list")
    public ApiResponse<?> uploadWorkbookList(
            @AuthenticationPrincipal User user,
            @RequestBody WorkbookUploadList request
    ){

        return new ApiResponse<>(HttpStatus.OK, "문제집 리스트 업로드 성공", null);
    }

    @Operation(summary = "문제집 다운로드", description = "문제집을 다운로드합니다.")
    @PostMapping("/{workbookId}/download")
    public ApiResponse<?> downloadWorkbook(
            @AuthenticationPrincipal User user,
            @PathVariable("workbookId") Long workbookId
    ){
        Workbook workbook = workbookService.downloadWorkbook(workbookId, user);
        WorkbookSimpleResponse response = WorkbookSimpleResponse.from(workbook);
        return new ApiResponse<>(HttpStatus.OK, "문제집 다운로드 성공", response);
    }

    @Operation(summary = "문제집 병합 페이지 로드", description = "문제집 병합 페이지를 로드합니다.")
    @GetMapping("/merge")
    public ApiResponse<?> mergeWorkbookPageLoad(
            @AuthenticationPrincipal User user,
            @RequestParam("ids") List<Long> workbookIds
    ){
        boolean isvalid = workbookIds.stream()
                // 하나라도 isInUserFolders == false가 있으면 allMatch 전체가 false
                .allMatch(id -> folderService.isWorkbookInUserFolder(user, id));
        if(!isvalid) {
            throw new RuntimeException("문제집이 유저의 폴더에 존재하지 않습니다.");
        }
        List<Problem> problems = problemService.findAllByWorkbookIds(workbookIds);
        WorkbookMergePageResponse response = WorkbookMergePageResponse.from(problems);
        return new ApiResponse<>(HttpStatus.OK, "문제집 병합 페이지 로드 성공", response);
    }

    @Operation(summary = "문제집 병합", description = "여러 문제집을 병합합니다.")
    @PostMapping("/merge")
    public ApiResponse<?> mergeWorkbook(
            @AuthenticationPrincipal User user,
            @RequestBody WorkbookMergeRequest request
    ){
        Workbook workbook = workbookService.createWorkbookFromProblems(request.title(), request.problems(), user);
        WorkbookResponse response = WorkbookResponse.of(workbook);
        return new ApiResponse<>(HttpStatus.OK, "문제집 병합 성공", response);
    }

    @Operation(summary = "문제집 문제 조회 (문제집 편집용)", description = "문제집의 문제를 조회합니다.")
    @GetMapping("/{workbookId}/problems")
    public ApiResponse<?> getProblems(
            @AuthenticationPrincipal User user,
            @PathVariable("workbookId") Long workbookId
    ) {
        log.info("문제집 문제 조회 요청 : {}", workbookId);
        if(!folderService.isWorkbookInUserFolder(user, workbookId)) {
            throw new RuntimeException("문제집 접근 권한이 없습니다.");
        }
        Workbook workbook = workbookService.findWorkbookByIdWithProblems(workbookId);
        List<Problem> problems = workbook.getProblemWorkbookMaps().stream()
                .map(ProblemWorkbookMap::getProblem)
                .toList();
        WorkbookMergePageResponse response = WorkbookMergePageResponse.from(problems);
        return new ApiResponse<>(HttpStatus.OK, "문제집 문제 조회 성공", response);
    }

    @Operation(summary = "문제집 문제 수정", description = "문제집의 문제를 수정합니다.")
    @PatchMapping("/{workbookId}/problems")
    public ApiResponse<?> updateProblems(
            @AuthenticationPrincipal User user,
            @PathVariable("workbookId") Long workbookId,
            @RequestBody WorkbookUpdateProblemsRequest request
    ) {
        log.info("문제집 문제 수정 요청 : {}", request);
        if(!folderService.isWorkbookInUserFolder(user, workbookId)) {
            throw new RuntimeException("문제집 접근 권한이 없습니다.");
        }
        solveLogService.deleteAllLogByWorkbook(workbookId);
        Workbook workbook = workbookService.replaceProblemsInWorkbook(workbookId, request.problems(), user);
        WorkbookSimpleResponse response = WorkbookSimpleResponse.from(workbook);
        return new ApiResponse<>(HttpStatus.OK, "문제집 문제 수정 성공", response);
    }

    @PostMapping("/{workbookId}/vote")
    @Operation(summary = "문제집 좋아요 / 싫어요", description = "문제집에 좋아요 / 싫어요를 누릅니다. (like / dislike)")
    public ApiResponse<?> voteWorkbook(
            @AuthenticationPrincipal User user,
            @PathVariable("workbookId") Long workbookId,
            @RequestBody WorkbookVoteRequest request
    ) {
        log.info("문제집 투표 요청 : {}", request);
        Workbook workbook = workbookService.findWorkbookById(workbookId);
        if(!publicFolderService.isPublicWorkbook(workbookId)) {
            throw new RuntimeException("문제집 접근 권한이 없습니다.");
        }
        workbookVoteService.toggleVote(workbook, user, request.voteType());
        workbook = workbookVoteService.updateVoteCount(workbook);
        WorkbookSimpleResponse response = WorkbookSimpleResponse.from(workbook);
        return new ApiResponse<>(HttpStatus.OK, "문제집 투표 성공", response);
    }
}
