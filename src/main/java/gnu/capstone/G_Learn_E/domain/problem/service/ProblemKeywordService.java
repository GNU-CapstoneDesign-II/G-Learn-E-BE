package gnu.capstone.G_Learn_E.domain.problem.service;

import gnu.capstone.G_Learn_E.domain.problem.dto.response.KeywordFrequencyResponse;
import gnu.capstone.G_Learn_E.domain.problem.entity.FailedKeywordGenerate;
import gnu.capstone.G_Learn_E.domain.problem.entity.Problem;
import gnu.capstone.G_Learn_E.domain.problem.entity.ProblemKeyword;
import gnu.capstone.G_Learn_E.domain.problem.repository.FailedKeywordGenerateRepository;
import gnu.capstone.G_Learn_E.domain.problem.repository.ProblemKeywordRepository;
import gnu.capstone.G_Learn_E.domain.problem.repository.ProblemRepository;
import gnu.capstone.G_Learn_E.domain.solve_log.entity.SolveLog;
import gnu.capstone.G_Learn_E.domain.solve_log.enums.SolvingStatus;
import gnu.capstone.G_Learn_E.domain.solve_log.repository.SolveLogRepository;
import gnu.capstone.G_Learn_E.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProblemKeywordService {

    private final ProblemRepository problemRepository;
    private final ProblemKeywordRepository problemKeywordRepository;
    private final FailedKeywordGenerateRepository failedKeywordGenerateRepository;

    private final SolveLogRepository solveLogRepository;


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveProblemKeywords(Long problemId, List<String> keywords) {
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new IllegalArgumentException("Problem not found with id: " + problemId));

        AtomicInteger priority = new AtomicInteger(1);

        List<ProblemKeyword> problemKeywords = keywords.stream()
                .map(keyword -> ProblemKeyword.builder()
                        .priority(priority.getAndIncrement())
                        .problem(problem)
                        .keyword(keyword)
                        .build())
                .toList();
        problemKeywordRepository.saveAll(problemKeywords);
    }

    public List<Problem> findProblemsByProblemKeywordsIsEmpty(int page, int size) {
        return problemRepository.findByProblemKeywordsIsEmpty(PageRequest.of(page, size)).getContent();
    }

    @Transactional
    public void saveFailedKeywordGenerate(Long problemId) {
        failedKeywordGenerateRepository.save(FailedKeywordGenerate.builder()
                .problemId(problemId)
                .build());
    }

    @Transactional
    public void saveFailedKeywordGenerate(List<Long> problemIds) {
        List<FailedKeywordGenerate> failedKeywordGenerates = problemIds.stream()
                .map(problemId -> FailedKeywordGenerate.builder()
                        .problemId(problemId)
                        .build())
                .toList();
        failedKeywordGenerateRepository.saveAll(failedKeywordGenerates);
    }

    public List<KeywordFrequencyResponse> getTopWrongKeywords(User user, int topN) {
        // 1) 완료된 풀로그 전체 조회
        List<SolveLog> solveLogs = solveLogRepository
                .findAllCompletedSolveLogs( // 혹은 findAllCompletedSolveLogs
                        user.getId(),
                        SolvingStatus.COMPLETED
                );

        // 2) 오답 문제 ID만 뽑아서 중복 제거
        List<Long> wrongProblemIds = solveLogs.stream()
                .filter(sl -> Boolean.FALSE.equals(sl.getIsCorrect()))
                .map(SolveLog::getProblemId)
                .distinct()
                .toList();

        if (wrongProblemIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 3) 해당 문제들의 키워드들 모두 조회
        List<ProblemKeyword> keywords = problemKeywordRepository
                .findAllByProblem_IdIn(wrongProblemIds);

        // 4) 키워드별 빈도수 집계
        Map<String, Long> freqMap = keywords.stream()
                .collect(Collectors.groupingBy(
                        ProblemKeyword::getKeyword,
                        Collectors.counting()
                ));

        // 5) 빈도 내림차순 정렬 후 상위 N개를 DTO로 변환
        return freqMap.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder()))
                .limit(topN)
                .map(e -> KeywordFrequencyResponse.of(e.getKey(), e.getValue()))
                .toList();
    }
}
