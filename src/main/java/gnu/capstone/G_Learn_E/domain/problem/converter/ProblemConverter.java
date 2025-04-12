package gnu.capstone.G_Learn_E.domain.problem.converter;

import gnu.capstone.G_Learn_E.domain.problem.dto.response.ProblemResponse;
import gnu.capstone.G_Learn_E.domain.problem.dto.response.ProblemSolvePageResponse;
import gnu.capstone.G_Learn_E.domain.problem.entity.Problem;
import gnu.capstone.G_Learn_E.domain.problem.enums.ProblemType;
import gnu.capstone.G_Learn_E.domain.solve_log.entity.SolveLog;
import gnu.capstone.G_Learn_E.domain.solve_log.entity.SolvedWorkbook;
import gnu.capstone.G_Learn_E.domain.solve_log.enums.SolvingStatus;
import gnu.capstone.G_Learn_E.domain.workbook.dto.response.ProblemGenerateResponse;
import gnu.capstone.G_Learn_E.domain.workbook.entity.Workbook;
import gnu.capstone.G_Learn_E.global.common.serialization.Option;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class ProblemConverter {

    /**
     * 객관식 문제를 Problem 엔티티로 변환합니다.
     * @param mc 객관식 문제 DTO
     * @return 변환된 Problem 엔티티
     */
    public static Problem convertToMultipleProblem(
            ProblemGenerateResponse.MultipleChoice mc, Integer problemNumber, Workbook workbook) {
        return Problem.builder()
                .problemNumber(problemNumber)
                .title(mc.question())
                .options(convertOptions(mc.options()))
                .answers(Collections.singletonList(mc.answer()))
                .explanation(mc.explanation())
                .type(ProblemType.MULTIPLE)
                .workbook(workbook)
                .build();
    }


    /**
     * OX 문제를 Problem 엔티티로 변환합니다.
     * @param ox OX 문제 DTO
     * @return 변환된 Problem 엔티티
     */
    public static Problem convertToOxProblem(
            ProblemGenerateResponse.Ox ox, Integer problemNumber, Workbook workbook) {
        return Problem.builder()
                .problemNumber(problemNumber)
                .title(ox.question())
                .options(null)
                .answers(Collections.singletonList(ox.answer()))
                .explanation(ox.explanation())
                .type(ProblemType.OX)
                .workbook(workbook)
                .build();
    }

    /**
     * 빈칸 채우기 문제를 Problem 엔티티로 변환합니다.
     * @param fib 빈칸 채우기 문제 DTO
     * @return 변환된 Problem 엔티티
     */
    public static Problem convertToBlankProblem(
            ProblemGenerateResponse.FillInTheBlank fib, Integer problemNumber, Workbook workbook) {
        return Problem.builder()
                .problemNumber(problemNumber)
                .title(fib.question())
                .options(null)
                .answers(fib.answer())
                .explanation(fib.explanation())
                .type(ProblemType.BLANK)
                .workbook(workbook)
                .build();
    }

    /**
     * 서술형 문제를 Problem 엔티티로 변환합니다.
     * @param desc 서술형 문제 DTO
     * @return 변환된 Problem 엔티티
     */
    public static Problem convertToDescriptiveProblem(
            ProblemGenerateResponse.Descriptive desc, Integer problemNumber, Workbook workbook) {
        return Problem.builder()
                .problemNumber(problemNumber)
                .title(desc.question())
                .options(null)
                .answers(Collections.singletonList(desc.answer()))
                // 서술형 문제는 해설이 없는 경우도 있음
                .explanation(null)
                .type(ProblemType.DESCRIPTIVE)
                .workbook(workbook)
                .build();
    }


    /**
     * 문자열 목록을 Option 객체의 목록으로 변환합니다.
     * @param optionsList 객관식 보기 문자열 목록
     * @return Option 객체 목록
     */
    private static List<Option> convertOptions(List<String> optionsList) {
        if (optionsList == null) {
            return null;
        }

        return IntStream.range(0, optionsList.size())
                .mapToObj(i -> Option.builder()
                        .number((short) (i + 1)) // 1부터 시작하는 번호
                        .content(optionsList.get(i))
                        .build())
                .collect(Collectors.toList());
    }



    public static ProblemSolvePageResponse convertToProblemSolvePageResponse(
            Workbook workbook,
            List<Problem> problems,
            SolvedWorkbook solvedWorkbook,
            Map<Long, SolveLog> solveLogToMap
    ) {
        boolean isSolved = solvedWorkbook.getStatus().equals(SolvingStatus.COMPLETED);
        AtomicInteger correctCount = new AtomicInteger();
        AtomicInteger wrongCount = new AtomicInteger();

        // 문제 정보와 문제 풀이 기록을 매핑하여 dto로 변환
        List<ProblemSolvePageResponse.ProblemInfo> problemInfoList =
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
                            ProblemSolvePageResponse.ProblemInfo.UserAttempt userAttempt =
                                    ProblemSolvePageResponse.ProblemInfo.UserAttempt.of(
                                            (!solveLog.getSubmitAnswer().isEmpty())? solveLog.getSubmitAnswer() : null,
                                            (isSolved) ? solveLog.getIsCorrect() : null
                                    );

                            // 문제 정보와 풀이 정보를 결합하여 반환
                            return ProblemSolvePageResponse.ProblemInfo.from(
                                    problemResponse,
                                    userAttempt
                            );
                        })
                        .toList();

        for(ProblemSolvePageResponse.ProblemInfo problemInfo : problemInfoList) {
            log.info("문제 정보 : {}", problemInfo.problem());
        }


        return ProblemSolvePageResponse.from(
                ProblemSolvePageResponse.WorkbookInfo.of(
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
