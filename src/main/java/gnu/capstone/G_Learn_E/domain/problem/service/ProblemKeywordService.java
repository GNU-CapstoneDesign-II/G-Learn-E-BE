package gnu.capstone.G_Learn_E.domain.problem.service;

import gnu.capstone.G_Learn_E.domain.problem.entity.FailedKeywordGenerate;
import gnu.capstone.G_Learn_E.domain.problem.entity.Problem;
import gnu.capstone.G_Learn_E.domain.problem.entity.ProblemKeyword;
import gnu.capstone.G_Learn_E.domain.problem.repository.FailedKeywordGenerateRepository;
import gnu.capstone.G_Learn_E.domain.problem.repository.ProblemKeywordRepository;
import gnu.capstone.G_Learn_E.domain.problem.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProblemKeywordService {

    private final ProblemRepository problemRepository;
    private final ProblemKeywordRepository problemKeywordRepository;
    private final FailedKeywordGenerateRepository failedKeywordGenerateRepository;


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
}
