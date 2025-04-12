package gnu.capstone.G_Learn_E.domain.workbook.controller;

import gnu.capstone.G_Learn_E.domain.workbook.converter.WorkbookConverter;
import gnu.capstone.G_Learn_E.domain.workbook.dto.response.WorkbookSolveResponse;
import gnu.capstone.G_Learn_E.domain.problem.entity.Problem;
import gnu.capstone.G_Learn_E.domain.problem.service.ProblemService;
import gnu.capstone.G_Learn_E.domain.solve_log.entity.SolveLog;
import gnu.capstone.G_Learn_E.domain.solve_log.entity.SolvedWorkbook;
import gnu.capstone.G_Learn_E.domain.solve_log.service.SolveLogService;
import gnu.capstone.G_Learn_E.domain.user.entity.User;
import gnu.capstone.G_Learn_E.domain.workbook.dto.request.ProblemGenerateRequest;
import gnu.capstone.G_Learn_E.domain.workbook.dto.response.WorkbookResponse;
import gnu.capstone.G_Learn_E.domain.workbook.entity.Workbook;
import gnu.capstone.G_Learn_E.domain.workbook.service.WorkbookService;
import gnu.capstone.G_Learn_E.domain.workbook.dto.response.ProblemGenerateResponse;
import gnu.capstone.G_Learn_E.global.fastapi.service.FastApiService;
import gnu.capstone.G_Learn_E.global.template.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/workbook")
@RequiredArgsConstructor
public class WorkbookController {

    private final WorkbookService workbookService;
    private final ProblemService problemService;
    private final SolveLogService solveLogService;
    private final FastApiService fastApiService;

    // TODO : 문제집 컨트롤러 구현


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, path = "/generate")
    public ApiResponse<WorkbookResponse> createWorkbook(
            @AuthenticationPrincipal User user,
            @ModelAttribute ProblemGenerateRequest request
    ) {


        ProblemGenerateResponse problemGenerateResponse = fastApiService.makeDummyResponse1(request);
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
        Workbook workbook = workbookService.findWorkbookById(workbookId);
        log.info("문제집 조회 성공 : {}", workbook);

        List<Problem> problems = problemService.findAllByWorkbookId(workbook);
        log.info("문제 조회 성공 : {}", problems);

        SolvedWorkbook solvedWorkbook = solveLogService.findSolvedWorkbook(workbook, user);
        log.info("문제집 풀이 기록 조회 성공 : {}", solvedWorkbook);

        Map<Long, SolveLog> solveLogToMap = solveLogService.findAllSolveLogToMap(solvedWorkbook);
        log.info("문제 풀이 기록 조회 성공 : {}", solveLogToMap);

        WorkbookSolveResponse response = WorkbookConverter.convertToWorkbookSolveResponse(
                workbook,
                problems,
                solvedWorkbook,
                solveLogToMap
        );
        log.info("문제 풀이 페이지 로드 성공 : {}", response);
        return new ApiResponse<>(HttpStatus.OK, "문제 풀이 페이지 로드 성공", response);
    }
}
