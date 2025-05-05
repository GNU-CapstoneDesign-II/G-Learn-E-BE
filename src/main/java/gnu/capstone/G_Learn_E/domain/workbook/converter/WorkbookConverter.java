package gnu.capstone.G_Learn_E.domain.workbook.converter;

import gnu.capstone.G_Learn_E.domain.problem.dto.response.ProblemResponse;
import gnu.capstone.G_Learn_E.domain.problem.entity.Problem;
import gnu.capstone.G_Learn_E.domain.problem.entity.ProblemWorkbookMap;
import gnu.capstone.G_Learn_E.domain.solve_log.entity.SolveLog;
import gnu.capstone.G_Learn_E.domain.solve_log.entity.SolvedWorkbook;
import gnu.capstone.G_Learn_E.domain.solve_log.enums.SolvingStatus;
import gnu.capstone.G_Learn_E.domain.workbook.dto.response.WorkbookSolveResponse;
import gnu.capstone.G_Learn_E.domain.workbook.entity.Workbook;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


@Slf4j
public class WorkbookConverter {


    public static WorkbookSolveResponse convertToWorkbookSolveResponse(
            Workbook workbook,
            SolvedWorkbook solvedWorkbook,
            List<ProblemWorkbookMap> problemWorkbookMaps,
            Map<Long, SolveLog> solveLogMap
    ) {
        boolean isSolved = solvedWorkbook.getStatus() == SolvingStatus.COMPLETED;
        AtomicInteger correctCount = new AtomicInteger();
        AtomicInteger wrongCount   = new AtomicInteger();

        // 매핑된 순서(problemNumber) 기준 정렬 후 DTO 변환
        List<WorkbookSolveResponse.ProblemInfo> problemInfoList = problemWorkbookMaps.stream()
                .sorted(Comparator.comparing(ProblemWorkbookMap::getProblemNumber))
                .map(map -> {
                    Problem problem = map.getProblem();
                    SolveLog solveLog = solveLogMap.get(problem.getId());

                    if (isSolved && Boolean.TRUE.equals(solveLog.getIsCorrect())) {
                        correctCount.getAndIncrement();
                    } else if (isSolved) {
                        wrongCount.getAndIncrement();
                    }

                    ProblemResponse pr = ProblemResponse.from(map);
                    WorkbookSolveResponse.ProblemInfo.UserAttempt ua =
                            WorkbookSolveResponse.ProblemInfo.UserAttempt.of(
                                    !solveLog.getSubmitAnswer().isEmpty() ? solveLog.getSubmitAnswer() : null,
                                    isSolved ? solveLog.getIsCorrect() : null
                            );

                    return WorkbookSolveResponse.ProblemInfo.from(pr, ua);
                })
                .toList();

        return WorkbookSolveResponse.from(
                WorkbookSolveResponse.WorkbookInfo.of(
                        workbook.getId(),
                        workbook.getName(),
                        isSolved,
                        isSolved ? correctCount.get() : null,
                        isSolved ? wrongCount.get() : null
                ),
                problemInfoList
        );
    }

}
