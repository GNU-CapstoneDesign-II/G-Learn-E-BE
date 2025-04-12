package gnu.capstone.G_Learn_E.domain.solve_log.service;

import gnu.capstone.G_Learn_E.domain.problem.repository.ProblemRepository;
import gnu.capstone.G_Learn_E.domain.solve_log.dto.request.SaveSolveLogRequest;
import gnu.capstone.G_Learn_E.domain.solve_log.dto.request.SolveLogRequest;
import gnu.capstone.G_Learn_E.domain.solve_log.entity.SolveLog;
import gnu.capstone.G_Learn_E.domain.solve_log.entity.SolvedWorkbook;
import gnu.capstone.G_Learn_E.domain.solve_log.entity.SolvedWorkbookId;
import gnu.capstone.G_Learn_E.domain.solve_log.repository.SolveLogRepository;
import gnu.capstone.G_Learn_E.domain.solve_log.repository.SolvedWorkbookRepository;
import gnu.capstone.G_Learn_E.domain.user.entity.User;
import gnu.capstone.G_Learn_E.domain.workbook.entity.Workbook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SolveLogService {

    private final ProblemRepository problemRepository;
    private final SolveLogRepository solveLogRepository;
    private final SolvedWorkbookRepository solvedWorkbookRepository;

    // TODO : 풀이 로그 서비스 구현

    @Transactional
    public SolvedWorkbook findSolvedWorkbook(Workbook workbook, User user) {
        SolvedWorkbookId solvedWorkbookId = new SolvedWorkbookId(user.getId(), workbook.getId());
        log.info("solvedWorkbookId : {}", solvedWorkbookId);

        return solvedWorkbookRepository.findById(solvedWorkbookId).orElseGet(
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
        log.info("solvedWorkbook : {}", solvedWorkbook);


        problemRepository.findAllByWorkbookId(workbook.getId()).forEach(problem -> {
            SolveLog solveLog = SolveLog.builder()
                    .solvedWorkbook(solvedWorkbook)
                    .problem(problem)
                    .build();
            solveLogRepository.save(solveLog);
        });
        log.info("solveLog : {}", solveLogRepository.findAllBySolvedWorkbookId(solvedWorkbook.getId()));

        return solvedWorkbook;
    }

    @Transactional
    public void saveAllSolveLog(List<SolveLog> solveLogs) {
        // 문제 풀이 로그 저장
        solveLogRepository.saveAll(solveLogs);
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
}
