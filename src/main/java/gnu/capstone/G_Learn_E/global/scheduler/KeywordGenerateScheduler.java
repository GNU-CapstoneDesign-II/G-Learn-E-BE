package gnu.capstone.G_Learn_E.global.scheduler;


import gnu.capstone.G_Learn_E.domain.problem.entity.Problem;
import gnu.capstone.G_Learn_E.domain.problem.entity.ProblemKeyword;
import gnu.capstone.G_Learn_E.domain.problem.repository.ProblemKeywordRepository;
import gnu.capstone.G_Learn_E.domain.problem.repository.ProblemRepository;
import gnu.capstone.G_Learn_E.global.fastapi.dto.response.ExtractKeywordsResponse;
import gnu.capstone.G_Learn_E.global.fastapi.service.FastApiService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@RequiredArgsConstructor
public class KeywordGenerateScheduler {

    private final ProblemRepository problemRepository;
    private final ProblemKeywordRepository problemKeywordRepository;
    private final FastApiService fastApiService;

    @Value("${keywords-per-problem}")
    private int keywordsPerProblem;

    @Transactional
    public void keywordGenerate() {
        // TODO : 예외 발생 시 다음 문제 키워드 추출 안되는 구조 수정해야 함
        List<Problem> problems = problemRepository.findByProblemKeywordsIsEmpty(PageRequest.of(0, 5)).getContent();
        ExtractKeywordsResponse extractKeywordsResponse = fastApiService.extractKeywordsFromProblems(problems, keywordsPerProblem);

        for(ExtractKeywordsResponse.ProblemKeyword problemKeyword : extractKeywordsResponse.problems()) {
            saveProblemKeywords(problemKeyword.id(), problemKeyword.keywords());
        }

    }

    @Transactional
    protected void saveProblemKeywords(Long problemId, List<String> keywords) {
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

}
