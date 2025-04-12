package gnu.capstone.G_Learn_E.domain.workbook.converter;

import gnu.capstone.G_Learn_E.domain.problem.dto.response.ProblemResponse;
import gnu.capstone.G_Learn_E.domain.problem.entity.Problem;
import gnu.capstone.G_Learn_E.domain.solve_log.entity.SolveLog;
import gnu.capstone.G_Learn_E.domain.solve_log.entity.SolvedWorkbook;
import gnu.capstone.G_Learn_E.domain.solve_log.enums.SolvingStatus;
import gnu.capstone.G_Learn_E.domain.workbook.dto.response.ProblemGenerateResponse;
import gnu.capstone.G_Learn_E.domain.workbook.dto.response.WorkbookSolveResponse;
import gnu.capstone.G_Learn_E.domain.workbook.entity.Workbook;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static gnu.capstone.G_Learn_E.domain.problem.converter.ProblemConverter.*;

@Slf4j
public class WorkbookConverter {

    /**
     * ProblemGenerateResponse DTO를 기반으로 Workbook 객체와 해당 Workbook에 포함될 Problem 엔티티들을 생성합니다.
     *
     * @param response Problem 생성 DTO
     * @param workbook 변환 대상 Workbook 엔티티 (기본 정보는 이미 셋팅되어 있다고 가정)
     * @return 문제들이 추가된 Workbook 객체
     */
    public static Workbook convertToWorkbookAndProblems(ProblemGenerateResponse response, Workbook workbook) {
        List<Problem> problems = new ArrayList<>();

        Integer problemNumber = 1; // 문제 번호 초기화

        // 1. 객관식 문제 변환
        for (ProblemGenerateResponse.MultipleChoice mc : response.result().multipleChoice()) {
            Problem problem = convertToMultipleProblem(mc, problemNumber++, workbook);
            problems.add(problem);
        }

        // 2. OX 문제 변환
        for (ProblemGenerateResponse.Ox ox : response.result().ox()) {
            Problem problem = convertToOxProblem(ox, problemNumber++, workbook);
            problems.add(problem);
        }

        // 3. 빈칸 채우기 문제 변환
        for (ProblemGenerateResponse.FillInTheBlank fib : response.result().fillInTheBlank()) {
            Problem problem = convertToBlankProblem(fib, problemNumber++, workbook);
            problems.add(problem);
        }

        // 4. 서술형 문제 변환
        for (ProblemGenerateResponse.Descriptive desc : response.result().descriptive()) {
            Problem problem = convertToDescriptiveProblem(desc, problemNumber++, workbook);
            problems.add(problem);
        }

        // Workbook의 문제 목록에 추가 (양방향 연관관계 설정)
        workbook.getProblems().addAll(problems);
        return workbook;
    }


    public static WorkbookSolveResponse convertToWorkbookSolveResponse(
            Workbook workbook,
            List<Problem> problems,
            SolvedWorkbook solvedWorkbook,
            Map<Long, SolveLog> solveLogToMap
    ) {
        boolean isSolved = solvedWorkbook.getStatus().equals(SolvingStatus.COMPLETED);
        AtomicInteger correctCount = new AtomicInteger();
        AtomicInteger wrongCount = new AtomicInteger();

        // 문제 정보와 문제 풀이 기록을 매핑하여 dto로 변환
        List<WorkbookSolveResponse.ProblemInfo> problemInfoList =
                problems.stream()
                        .sorted(Comparator.comparing(Problem::getProblemNumber)) // 문제 번호로 정렬
                        .map(problem -> {
                            // 문제집의 문제 순회

                            // 문제 풀이 기록 매핑
                            SolveLog solveLog = solveLogToMap.get(problem.getId());
                            if (isSolved && solveLog.getIsCorrect()) { // 풀이가 완료된 경우
                                correctCount.getAndIncrement(); // 정답 개수 증가
                            } else if(isSolved) {
                                wrongCount.getAndIncrement(); // 오답 개수 증가
                            }

                            // 문제 정보 변환
                            ProblemResponse problemResponse = ProblemResponse.from(problem);

                            // 문제 풀이 정보 변환
                            WorkbookSolveResponse.ProblemInfo.UserAttempt userAttempt =
                                    WorkbookSolveResponse.ProblemInfo.UserAttempt.of(
                                            (!solveLog.getSubmitAnswer().isEmpty())? solveLog.getSubmitAnswer() : null,
                                            (isSolved) ? solveLog.getIsCorrect() : null
                                    );

                            // 문제 정보와 풀이 정보를 결합하여 반환
                            return WorkbookSolveResponse.ProblemInfo.from(
                                    problemResponse,
                                    userAttempt
                            );
                        })
                        .toList();

        for(WorkbookSolveResponse.ProblemInfo problemInfo : problemInfoList) {
            log.info("문제 정보 : {}", problemInfo.problem());
        }


        return WorkbookSolveResponse.from(
                WorkbookSolveResponse.WorkbookInfo.of(
                        workbook.getId(),
                        workbook.getName(),
                        isSolved,
                        (isSolved) ? correctCount.get() : null,
                        (isSolved) ? wrongCount.get() : null
                ),
                problemInfoList
        );
    }
}
