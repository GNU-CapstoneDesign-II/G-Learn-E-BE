package gnu.capstone.G_Learn_E.domain.solve_log.service;

import gnu.capstone.G_Learn_E.domain.problem.entity.Problem;
import gnu.capstone.G_Learn_E.domain.problem.entity.ProblemWorkbookMap;
import gnu.capstone.G_Learn_E.domain.problem.enums.ProblemType;
import gnu.capstone.G_Learn_E.domain.problem.repository.ProblemWorkbookMapRepository;
import gnu.capstone.G_Learn_E.domain.solve_log.dto.request.SaveSolveLogRequest;
import gnu.capstone.G_Learn_E.domain.solve_log.dto.request.SolveLogRequest;
import gnu.capstone.G_Learn_E.domain.solve_log.entity.SolveLog;
import gnu.capstone.G_Learn_E.domain.solve_log.entity.SolvedWorkbook;
import gnu.capstone.G_Learn_E.domain.solve_log.entity.SolvedWorkbookId;
import gnu.capstone.G_Learn_E.domain.solve_log.enums.SolvingStatus;
import gnu.capstone.G_Learn_E.domain.solve_log.repository.SolveLogRepository;
import gnu.capstone.G_Learn_E.domain.solve_log.repository.SolvedWorkbookRepository;
import gnu.capstone.G_Learn_E.domain.user.entity.User;
import gnu.capstone.G_Learn_E.domain.workbook.dto.response.GradeWorkbookResponse;
import gnu.capstone.G_Learn_E.domain.workbook.entity.Workbook;
import gnu.capstone.G_Learn_E.global.fastapi.dto.request.GradeBlankRequest;
import gnu.capstone.G_Learn_E.global.fastapi.dto.request.GradeDescriptiveRequest;
import gnu.capstone.G_Learn_E.global.fastapi.dto.response.GradeBlankResponse;
import gnu.capstone.G_Learn_E.global.fastapi.dto.response.GradeDescriptiveResponse;
import gnu.capstone.G_Learn_E.global.fastapi.service.FastApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SolveLogService {

    private final SolveLogRepository solveLogRepository;
    private final SolvedWorkbookRepository solvedWorkbookRepository;
    private final ProblemWorkbookMapRepository problemWorkbookMapRepository;
    private final FastApiService fastApiService;

    // TODO : 풀이 로그 서비스 구현

    @Transactional
    public SolvedWorkbook findSolvedWorkbook(Workbook workbook, User user) {
        return solvedWorkbookRepository.findById(new SolvedWorkbookId(user.getId(), workbook.getId())).orElseGet(
                () -> createSolveLogs(workbook, user) // solvedWorkbookId가 없으면 createSolveLogs 메서드 호출
        );
    }

    public List<SolveLog> findAllSolveLog(SolvedWorkbook solvedWorkbook) {
        return solveLogRepository.findAllBySolvedWorkbookId(solvedWorkbook.getId());
    }

    public Map<Long, SolveLog> findAllSolveLogToMap(SolvedWorkbook solvedWorkbook) {
        return solveLogRepository.findAllBySolvedWorkbookId(solvedWorkbook.getId())
                .stream()
                .collect(Collectors.toMap(SolveLog::getProblemId, Function.identity()));
    }

    @Transactional
    public SolvedWorkbook createSolveLogs(Workbook workbook, User user) {
        SolvedWorkbookId solvedWorkbookId = new SolvedWorkbookId(user.getId(), workbook.getId());

        SolvedWorkbook solvedWorkbook = solvedWorkbookRepository.findById(solvedWorkbookId)
                .orElseGet(() -> solvedWorkbookRepository.save(
                        SolvedWorkbook.builder()
                                .id(solvedWorkbookId)
                                .user(user)
                                .workbook(workbook)
                                .build()
                ));

        problemWorkbookMapRepository.findAllByWorkbook_IdOrderByProblemNumber(workbook.getId()).forEach(problem -> {
            SolveLog solveLog = SolveLog.builder()
                    .solvedWorkbook(solvedWorkbook)
                    .problem(problem.getProblem())
                    .build();
            solveLogRepository.save(solveLog);
        });

        return solvedWorkbook;
    }

    @Transactional
    public List<SolveLog> saveAllSolveLog(List<SolveLog> solveLogs) {
        // 문제 풀이 로그 저장
        return solveLogRepository.saveAll(solveLogs);
    }

    @Transactional
    public void updateSolveLog(SolvedWorkbook solvedWorkbook, SaveSolveLogRequest request){
        Map<Long, SolveLog> solveLogToMap = findAllSolveLogToMap(solvedWorkbook);

        List<SolveLog> logsToUpdate = new ArrayList<>();

        for (SolveLogRequest solveLogRequest : request.userAttempts()) {
            Long problemId = solveLogRequest.problemId();
            List<String> newAnswer = solveLogRequest.submitAnswer();

            SolveLog solveLog = solveLogToMap.get(problemId);
            if (solveLog == null) continue;

            List<String> currentAnswer = solveLog.getSubmitAnswer();

            // 기존 답안과 다를 때만 갱신
            if (!Objects.equals(currentAnswer, newAnswer)) {
                // 순서도 함께 비교
                solveLog.setSubmitAnswer(newAnswer);
                logsToUpdate.add(solveLog);
            }
        }

        saveAllSolveLog(logsToUpdate);
    }

    @Transactional
    public void deleteAllSolveLog(SolvedWorkbook solvedWorkbook) {
        List<SolveLog> solveLogs = findAllSolveLog(solvedWorkbook);
        // 문제 별 풀이 로그 삭제
        solveLogRepository.deleteAll(solveLogs);

        // 문제집 풀이 기록 삭제
        solvedWorkbookRepository.delete(solvedWorkbook);
    }

    @Transactional
    public GradeWorkbookResponse gradeWorkbook(
            User user,
            Workbook workbook,
            SaveSolveLogRequest request
    ) {
        // 1. SolvedWorkbook 조회 or 신규 생성
        SolvedWorkbook solvedWorkbook = findSolvedWorkbook(workbook, user);

        // 2. 사용자가 보낸 풀이 저장/업데이트
        updateSolveLog(solvedWorkbook, request);

        // 3. grading: 문제 목록과 정답 맵 생성
        Map<Long, Problem> problemMap = workbook.getProblemWorkbookMaps().stream()
                .map(ProblemWorkbookMap::getProblem)
                .collect(Collectors.toMap(Problem::getId, Function.identity()));

        // 4. 채점 수행
        return gradeAllSolveLog(solvedWorkbook, problemMap);
    }

    @Transactional
    public GradeWorkbookResponse gradeAllSolveLog(SolvedWorkbook solvedWorkbook, Map<Long, Problem> problemMap) {
        // 문제집 풀이 상태 업데이트 (진행 중)
        // 서술형, 빈칸 등의 채점은 GPT 이용으로 인해 시간이 걸릴 수 있으므로 Flush
        forceSetSolvingStatus(solvedWorkbook, SolvingStatus.IN_PROGRESS);
        try {
            List<SolveLog> solveLogs = findAllSolveLog(solvedWorkbook);
            Map<Long, SolveLog> blankSolveLogMap = new HashMap<>();
            Map<Long, SolveLog> descriptionSolveLogMap = new HashMap<>();

            for (SolveLog solveLog : solveLogs) {
                Problem problem = problemMap.get(solveLog.getProblemId());
                if (problem == null) continue;

                if (problem.getType().equals(ProblemType.BLANK)) {
                    blankSolveLogMap.put(solveLog.getId(), solveLog);
                } else if (problem.getType().equals(ProblemType.DESCRIPTIVE)) {
                    descriptionSolveLogMap.put(solveLog.getId(), solveLog);
                } else {
                    // 객관식, OX 문제 채점
                    if (Objects.equals(solveLog.getSubmitAnswer(), problem.getAnswers())) {
                        solveLog.setIsCorrect(true);
                        log.info("문제 id : {}, 정답 : {}, 채점 결과 : {}", problem.getId(), problem.getAnswers(), solveLog.getIsCorrect());
                    } else {
                        solveLog.setIsCorrect(false);
                        log.info("문제 id : {}, 정답 : {}, 채점 결과 : {}", problem.getId(), problem.getAnswers(), solveLog.getIsCorrect());
                    }
                }
            }

            // ✅ 병렬로 결과만 먼저 받아옴 (채점 자체는 여기서 수행, 반영은 아래서)
            log.info("빈칸 채점 비동기 처리 시작");
            CompletableFuture<List<GradeBlankResponse.GradedProblem>> blankFuture = null;
            if(!blankSolveLogMap.isEmpty()) {
                blankFuture = CompletableFuture.supplyAsync(() -> gradeBlankProblems(problemMap, blankSolveLogMap))
                        .exceptionally(e -> {
                            throw new RuntimeException("빈칸 채점 실패", e);
                        });
            }

            log.info("서술형 채점 비동기 처리 시작");
            CompletableFuture<List<GradeDescriptiveResponse.GradedProblem>> descFuture = null;
            if(!descriptionSolveLogMap.isEmpty()) {
                descFuture =
                        CompletableFuture.supplyAsync(() -> gradeDescriptionProblems(problemMap, descriptionSolveLogMap))
                                .exceptionally(e -> {
                                    throw new RuntimeException("서술형 채점 실패", e);
                                });
            }

            List<GradeBlankResponse.GradedProblem> blankResults = null;
            if(blankFuture != null) {
                 blankResults = blankFuture.join();
            }
            List<GradeDescriptiveResponse.GradedProblem> descResults = null;
            if(descFuture != null) {
                descResults = descFuture.join();
            }


            // ✅ 트랜잭션 안에서 결과 반영
            log.info("빈칸, 서술형 채점 결과 반영 시작");
            if(blankResults != null) {
                blankResults.forEach(gradedProblem -> {
                    SolveLog solveLog = blankSolveLogMap.get(gradedProblem.id());
                    if (solveLog == null) {
                        log.warn("빈칸 문제 풀이 로그 없음 : {}", gradedProblem.id());
                        throw new RuntimeException("빈칸 문제 풀이 로그 없음");
                    }
                    log.info("문제 id : {}, 채점 결과 : {}", gradedProblem.id(), gradedProblem.correct());
                    solveLog.setIsCorrect(gradedProblem.correct());
                });
            }
            if(descResults != null) {
                descResults.forEach(gradedProblem -> {
                    SolveLog solveLog = descriptionSolveLogMap.get(gradedProblem.id());
                    if (solveLog == null) {
                        log.warn("서술형 문제 풀이 로그 없음 : {}", gradedProblem.id());
                        throw new RuntimeException("서술형 문제 풀이 로그 없음");
                    }
                    log.info("문제 id : {}, 채점 결과 : {}", gradedProblem.id(), gradedProblem.correct());
                    solveLog.setIsCorrect(gradedProblem.correct());
                });
            }

            solveLogs = saveAllSolveLog(solveLogs);
            // 문제집 풀이 상태 업데이트
            solvedWorkbook.setStatus(SolvingStatus.COMPLETED);
            solvedWorkbookRepository.save(solvedWorkbook);

            // 문제 풀이 채점 결과 반환
            int correctCount = (int) solveLogs.stream()
                    .filter(SolveLog::getIsCorrect)
                    .count();
            int wrongCount = solveLogs.size() - correctCount;
            return GradeWorkbookResponse.of(correctCount, wrongCount);
        } catch (Exception e) {
            log.error("문제 풀이 채점 실패", e);
            forceSetSolvingStatus(solvedWorkbook, SolvingStatus.NOT_STARTED);
            throw new RuntimeException("문제 풀이 채점 실패", e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void forceSetSolvingStatus(SolvedWorkbook workbook, SolvingStatus status) {
        // 새로운 트랜잭션에서 상태 복구
        workbook.setStatus(status);
        solvedWorkbookRepository.save(workbook);
    }



    private List<GradeBlankResponse.GradedProblem> gradeBlankProblems(Map<Long, Problem> problemMap, Map<Long, SolveLog> blankSolveLogMap) {
        GradeBlankRequest request = GradeBlankRequest.of(
                blankSolveLogMap.values().stream()
                        .map(solveLog -> {
                                    Problem problem = problemMap.get(solveLog.getProblemId());
                                    return GradeBlankRequest.Problem.of(
                                            solveLog.getId(),
                                            problem.getTitle(),
                                            problem.getAnswers(),
                                            solveLog.getSubmitAnswer()
                                    );
                                }
                        )
                        .toList());
        return fastApiService.gradeBlank(request).result();
    }

    private List<GradeDescriptiveResponse.GradedProblem> gradeDescriptionProblems(Map<Long, Problem> problemMap, Map<Long, SolveLog> descriptionSolveLogMap) {
        GradeDescriptiveRequest request = GradeDescriptiveRequest.of(
                descriptionSolveLogMap.values().stream()
                        .map(solveLog -> {
                                    Problem problem = problemMap.get(solveLog.getProblemId());
                                    return GradeDescriptiveRequest.Problem.of(
                                            solveLog.getId(),
                                            problem.getTitle(),
                                            problem.getAnswers().getFirst(),
                                            solveLog.getSubmitAnswer().getFirst()
                                    );
                                }
                        )
                        .toList());
        return fastApiService.gradeDescriptive(request).result();
    }
}
