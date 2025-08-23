package gnu.capstone.G_Learn_E.domain.solve_log.controller;

import gnu.capstone.G_Learn_E.domain.folder.service.FolderService;
import gnu.capstone.G_Learn_E.domain.public_folder.service.PublicFolderService;
import gnu.capstone.G_Learn_E.domain.solve_log.dto.request.SaveSolveLogRequest;
import gnu.capstone.G_Learn_E.domain.solve_log.dto.response.SolveLogResponse;
import gnu.capstone.G_Learn_E.domain.solve_log.entity.SolveLog;
import gnu.capstone.G_Learn_E.domain.solve_log.entity.SolvedWorkbook;
import gnu.capstone.G_Learn_E.domain.solve_log.entity.SolvedWorkbookId;
import gnu.capstone.G_Learn_E.domain.solve_log.enums.SolvingStatus;
import gnu.capstone.G_Learn_E.domain.solve_log.service.SolveLogService;
import gnu.capstone.G_Learn_E.domain.user.entity.User;
import gnu.capstone.G_Learn_E.domain.workbook.entity.Workbook;
import gnu.capstone.G_Learn_E.domain.workbook.service.WorkbookService;
import gnu.capstone.G_Learn_E.global.template.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/solve-log")
@Tag(name = "풀이 로그 API")
@RequiredArgsConstructor
public class SolveLogController {

    private final WorkbookService workbookService;
    private final SolveLogService solveLogService;
    private final FolderService folderService;
    private final PublicFolderService publicFolderService;


    @GetMapping("/workbook/{workbookId}")
    @Operation(summary = "풀이 로그 조회", description = "풀이 로그를 조회합니다.")
    public ApiResponse<?> getUsersSolveLog(
            @AuthenticationPrincipal User user,
            @PathVariable("workbookId") Long workbookId
    ){
        if(!folderService.isWorkbookInUserFolder(user, workbookId) &&
                !publicFolderService.isPublicWorkbook(workbookId)) {
            throw new RuntimeException("문제집 접근 권한이 없습니다.");
        }

        Workbook workbook = workbookService.findWorkbookById(workbookId);
        SolvedWorkbook solvedWorkbook;
        try {
            solvedWorkbook = solveLogService.findSolvedWorkbookByIdWithSolveLogs(workbook, user);
        } catch (RuntimeException e) {
            // 풀이 로그가 없는 경우
            SolveLogResponse solveLogResponse = SolveLogResponse.of(
                    SolvingStatus.NOT_STARTED.getStatus(),
                    null,
                    0,
                    0
            );
            return new ApiResponse<>(HttpStatus.OK, "풀이 로그 조회 성공", solveLogResponse);
        }
        List<SolveLog> solveLogs = solvedWorkbook.getSolveLogs();
        int correctCount = 0 , wrongCount = 0;
        if(solvedWorkbook.getStatus() == SolvingStatus.COMPLETED) {
            for (SolveLog solveLog : solveLogs) {
                if (solveLog.getIsCorrect()) {
                    correctCount++;
                } else {
                    wrongCount++;
                }
            }
        }
        SolveLogResponse solveLogResponse = SolveLogResponse.of(
                solvedWorkbook.getStatus().getStatus(),
                solvedWorkbook.getUpdatedAt(),
                correctCount,
                wrongCount
        );
        return new ApiResponse<>(HttpStatus.OK, "풀이 로그 조회 성공", solveLogResponse);
    }

    @Operation(summary = "풀이 로그 업데이트", description = "풀이 로그를 업데이트합니다.")
    @PatchMapping("/workbook/{workbookId}")
    public ApiResponse<?> saveUserAnswer(
            @AuthenticationPrincipal User user,
            @PathVariable("workbookId") Long workbookId,
            @RequestBody SaveSolveLogRequest request
    ){
        if(!folderService.isWorkbookInUserFolder(user, workbookId) &&
                !publicFolderService.isPublicWorkbook(workbookId)) {
            throw new RuntimeException("문제집 접근 권한이 없습니다.");
        }
        List<SolveLog> solveLogs = solveLogService.findAllSolveLog(SolvedWorkbookId.builder()
                        .userId(user.getId())
                        .workbookId(workbookId)
                .build());

        solveLogService.updateSolveLog(solveLogs, request);

        return new ApiResponse<>(HttpStatus.OK, "풀이 로그 저장 성공", null);
    }

    @Operation(summary = "풀이 로그 삭제", description = "풀이 로그를 삭제합니다.")
    @DeleteMapping("/workbook/{workbookId}")
    public ApiResponse<?> deleteUsersSolveLog(
            @AuthenticationPrincipal User user,
            @PathVariable("workbookId") Long workbookId
    ){
        if(!folderService.isWorkbookInUserFolder(user, workbookId) &&
                !publicFolderService.isPublicWorkbook(workbookId)) {
            throw new RuntimeException("문제집 접근 권한이 없습니다.");
        }

        Workbook workbook = workbookService.findWorkbookById(workbookId);
        SolvedWorkbook solvedWorkbook = solveLogService.findSolvedWorkbookByIdWithSolveLogs(workbook, user);

        solveLogService.deleteAllSolveLog(solvedWorkbook);

        return new ApiResponse<>(HttpStatus.OK, "풀이 로그 삭제 성공", null);
    }
}
