package gnu.capstone.G_Learn_E.domain.solve_log.controller;

import gnu.capstone.G_Learn_E.domain.solve_log.dto.request.SaveSolveLogRequest;
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
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/api/solve-log")
@RequiredArgsConstructor
public class SolveLogController {

    private final WorkbookService workbookService;
    private final SolveLogService solveLogService;


    @PatchMapping("/workbook/{workbookId}")
    public ApiResponse<?> saveUserAnswer(
            @AuthenticationPrincipal User user,
            @PathVariable("workbookId") Long workbookId,
            @RequestBody SaveSolveLogRequest request
    ){
        Workbook workbook = workbookService.findWorkbookById(workbookId);
        SolvedWorkbook solvedWorkbook = solveLogService.findSolvedWorkbook(workbook, user);

        solveLogService.updateSolveLog(solvedWorkbook, request);

        return new ApiResponse<>(HttpStatus.OK, "풀이 로그 저장 성공", null);
    }

    @DeleteMapping("/workbook/{workbookId}")
    public ApiResponse<?> deleteUsersSolveLog(
            @AuthenticationPrincipal User user,
            @PathVariable("workbookId") Long workbookId
    ){
        Workbook workbook = workbookService.findWorkbookById(workbookId);
        SolvedWorkbook solvedWorkbook = solveLogService.findSolvedWorkbook(workbook, user);

        solveLogService.deleteAllSolveLog(solvedWorkbook);

        return new ApiResponse<>(HttpStatus.OK, "풀이 로그 삭제 성공", null);
    }
}
