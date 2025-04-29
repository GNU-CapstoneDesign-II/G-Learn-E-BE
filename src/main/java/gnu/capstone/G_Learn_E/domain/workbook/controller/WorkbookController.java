package gnu.capstone.G_Learn_E.domain.workbook.controller;

import gnu.capstone.G_Learn_E.domain.public_folder.entity.Subject;
import gnu.capstone.G_Learn_E.domain.public_folder.service.PublicFolderService;
import gnu.capstone.G_Learn_E.domain.solve_log.dto.request.SaveSolveLogRequest;
import gnu.capstone.G_Learn_E.domain.workbook.converter.WorkbookConverter;
import gnu.capstone.G_Learn_E.domain.workbook.dto.request.WorkbookUpload;
import gnu.capstone.G_Learn_E.domain.workbook.dto.request.WorkbookUploadList;
import gnu.capstone.G_Learn_E.domain.workbook.dto.response.*;
import gnu.capstone.G_Learn_E.domain.solve_log.entity.SolveLog;
import gnu.capstone.G_Learn_E.domain.solve_log.entity.SolvedWorkbook;
import gnu.capstone.G_Learn_E.domain.solve_log.service.SolveLogService;
import gnu.capstone.G_Learn_E.domain.user.entity.User;
import gnu.capstone.G_Learn_E.domain.workbook.dto.request.ProblemGenerateRequest;
import gnu.capstone.G_Learn_E.domain.workbook.entity.Workbook;
import gnu.capstone.G_Learn_E.domain.workbook.service.WorkbookService;
import gnu.capstone.G_Learn_E.global.fastapi.service.FastApiService;
import gnu.capstone.G_Learn_E.global.template.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/workbook")
@RequiredArgsConstructor
public class WorkbookController {

    private final WorkbookService workbookService;
    private final PublicFolderService publicFolderService;
    private final SolveLogService solveLogService;
    private final FastApiService fastApiService;


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

        // Workbook 생성 로직 처리 후 결과 반환
        return new ApiResponse<>(HttpStatus.OK, "문제집 생성에 성공하였습니다.", response);
    }

    /**
     * 문제 풀이 페이지 로드
     */
    @GetMapping("/{workbookId}/solve")
    public ApiResponse<WorkbookSolveResponse> problemSolvePageLoad(
            @AuthenticationPrincipal User user,
            @PathVariable("workbookId") Long workbookId
    ){
        Workbook workbook = workbookService.findUsersWorkbookByIdWithProblems(workbookId, user);
        log.info("문제집 조회 성공 : {}", workbook);

        SolvedWorkbook solvedWorkbook = solveLogService.findSolvedWorkbook(workbook, user);
        log.info("문제집 풀이 기록 조회 성공 : {}", solvedWorkbook);

        Map<Long, SolveLog> solveLogToMap = solveLogService.findAllSolveLogToMap(solvedWorkbook);
        log.info("문제 풀이 기록 조회 성공 : {}", solveLogToMap);

        WorkbookSolveResponse response = WorkbookConverter.convertToWorkbookSolveResponse(
                workbook,
                solvedWorkbook,
                workbook.getProblemWorkbookMaps(),
                solveLogToMap
        );
        log.info("문제 풀이 페이지 로드 성공 : {}", response);
        return new ApiResponse<>(HttpStatus.OK, "문제 풀이 페이지 로드 성공", response);
    }


    @GetMapping("/{workbookId}/solve/uploaded")
    public ApiResponse<WorkbookSolveResponse> problemSolvePageLoadUploaded(
            @AuthenticationPrincipal User user,
            @PathVariable("workbookId") Long workbookId
    ){
        Workbook workbook = workbookService.findWorkbookByIdWithProblems(workbookId);
        if(!workbook.isUploaded() || !publicFolderService.isPublicWorkbook(workbookId)) {
            throw new RuntimeException("업로드 된 문제집이 아닙니다.");
        }
        log.info("문제집 조회 성공 : {}", workbook);

        SolvedWorkbook solvedWorkbook = solveLogService.findSolvedWorkbook(workbook, user);
        log.info("문제집 풀이 기록 조회 성공 : {}", solvedWorkbook);

        Map<Long, SolveLog> solveLogToMap = solveLogService.findAllSolveLogToMap(solvedWorkbook);
        log.info("문제 풀이 기록 조회 성공 : {}", solveLogToMap);

        WorkbookSolveResponse response = WorkbookConverter.convertToWorkbookSolveResponse(
                workbook,
                solvedWorkbook,
                workbook.getProblemWorkbookMaps(),
                solveLogToMap
        );
        log.info("문제 풀이 페이지 로드 성공 : {}", response);
        return new ApiResponse<>(HttpStatus.OK, "문제 풀이 페이지 로드 성공", response);
    }



    @PostMapping("/{workbookId}/grade")
    public ApiResponse<?> gradeWorkbook(
            @AuthenticationPrincipal User user,
            @PathVariable("workbookId") Long workbookId,
            @RequestBody SaveSolveLogRequest request
    ){
        Workbook workbook = workbookService.findUsersWorkbookByIdWithProblems(workbookId, user);
        GradeWorkbookResponse response = solveLogService.gradeWorkbook(user, workbook, request);
        return new ApiResponse<>(HttpStatus.OK, "문제 풀이 채점 성공", response);
    }

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
        return new ApiResponse<>(HttpStatus.OK, "문제집 업로드 성공", response);
    }

    @PostMapping("/upload/list")
    public ApiResponse<?> uploadWorkbookList(
            @AuthenticationPrincipal User user,
            @RequestBody WorkbookUploadList request
    ){

        return new ApiResponse<>(HttpStatus.OK, "문제집 리스트 업로드 성공", null);
    }

    @PostMapping("/{workbookId}/download")
    public ApiResponse<?> downloadWorkbook(
            @AuthenticationPrincipal User user,
            @PathVariable("workbookId") Long workbookId
    ){
        Workbook workbook = workbookService.downloadWorkbook(workbookId, user);
        WorkbookDownloadResponse response = WorkbookDownloadResponse.of(workbook.getId());
        return new ApiResponse<>(HttpStatus.OK, "문제집 다운로드 성공", response);
    }

}
