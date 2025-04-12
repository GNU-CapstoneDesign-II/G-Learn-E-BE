package gnu.capstone.G_Learn_E.domain.problem.controller;

import gnu.capstone.G_Learn_E.domain.problem.converter.ProblemConverter;
import gnu.capstone.G_Learn_E.domain.problem.dto.response.ProblemSolvePageResponse;
import gnu.capstone.G_Learn_E.domain.problem.entity.Problem;
import gnu.capstone.G_Learn_E.domain.problem.service.ProblemService;
import gnu.capstone.G_Learn_E.domain.solve_log.entity.SolveLog;
import gnu.capstone.G_Learn_E.domain.solve_log.entity.SolvedWorkbook;
import gnu.capstone.G_Learn_E.domain.solve_log.service.SolveLogService;
import gnu.capstone.G_Learn_E.domain.user.entity.User;
import gnu.capstone.G_Learn_E.domain.workbook.entity.Workbook;
import gnu.capstone.G_Learn_E.domain.workbook.service.WorkbookService;
import gnu.capstone.G_Learn_E.global.template.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/problem")
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;
    private final WorkbookService workbookService;
    private final SolveLogService solveLogService;


    @GetMapping("/workbook/{workbookId}")
    public ApiResponse<ProblemSolvePageResponse> problemSolvePageLoad(
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

        ProblemSolvePageResponse response = ProblemConverter.convertToProblemSolvePageResponse(
                workbook,
                problems,
                solvedWorkbook,
                solveLogToMap
        );
        log.info("문제 풀이 페이지 로드 성공 : {}", response);
        return new ApiResponse<>(HttpStatus.OK, "문제 풀이 페이지 로드 성공", response);
    }
}
