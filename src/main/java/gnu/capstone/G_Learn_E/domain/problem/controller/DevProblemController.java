package gnu.capstone.G_Learn_E.domain.problem.controller;


import gnu.capstone.G_Learn_E.domain.problem.dto.request.ExtractKeywordsRequest;
import gnu.capstone.G_Learn_E.domain.problem.entity.Problem;
import gnu.capstone.G_Learn_E.domain.problem.service.ProblemKeywordService;
import gnu.capstone.G_Learn_E.global.fastapi.dto.response.ExtractKeywordsResponse;
import gnu.capstone.G_Learn_E.global.fastapi.service.FastApiService;
import gnu.capstone.G_Learn_E.global.template.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/dev/api/problem")
@RequiredArgsConstructor
public class DevProblemController {

    private final FastApiService fastApiService;
    private final ProblemKeywordService problemKeywordService;

    @Value("${keywords-per-problem}")
    private int keywordsPerProblem;


    @PostMapping("/extract-keywords")
    public ApiResponse<?> keywordGenerate(ExtractKeywordsRequest request) {
        // TODO : 예외 발생 시 다음 문제 키워드 추출 안되는 구조 수정해야 함
        List<Problem> problems = problemKeywordService.findProblemsByProblemKeywordsIsEmpty(request.page(), request.size());
        ExtractKeywordsResponse extractKeywordsResponse;
        try {
            extractKeywordsResponse = fastApiService.extractKeywordsFromProblems(problems, keywordsPerProblem);
        } catch (Exception e) {
            log.error("Error occurred while extracting keywords: {}", e.getMessage());
            log.error("Problem Ids: {}", problems.stream().map(Problem::getId).toList());
            List<Long> ids = problems.stream().map(Problem::getId).toList();
            problemKeywordService.saveFailedKeywordGenerate(ids);
            throw new RuntimeException("키워드 추출 중 오류가 발생했습니다.");
        }

        for (ExtractKeywordsResponse.ProblemKeyword problemKeyword : extractKeywordsResponse.problems()) {
            try {
                problemKeywordService.saveProblemKeywords(problemKeyword.id(), problemKeyword.keywords());
            } catch (Exception e) {
                problemKeywordService.saveFailedKeywordGenerate(problemKeyword.id());
                log.error("Error occurred while saving keywords for problem ID {}: {}", problemKeyword.id(), e.getMessage());
                throw new RuntimeException("키워드 저장 중 오류가 발생했습니다.");
            }
        }
        return new ApiResponse<>(HttpStatus.OK, "키워드 추출에 성공하였습니다.", extractKeywordsResponse);
    }

}
