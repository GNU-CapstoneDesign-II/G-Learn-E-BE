package gnu.capstone.G_Learn_E.global.fastapi.controller;

import gnu.capstone.G_Learn_E.domain.problem.entity.Problem;
import gnu.capstone.G_Learn_E.domain.problem.repository.ProblemRepository;
import gnu.capstone.G_Learn_E.domain.problem.service.ProblemService;
import gnu.capstone.G_Learn_E.domain.workbook.dto.request.QuestionTypes;
import gnu.capstone.G_Learn_E.domain.workbook.dto.response.ProblemGenerateResponse;
import gnu.capstone.G_Learn_E.global.fastapi.dto.request.GradeBlankRequest;
import gnu.capstone.G_Learn_E.global.fastapi.dto.request.GradeDescriptiveRequest;
import gnu.capstone.G_Learn_E.global.fastapi.dto.request.FastApiProblemGenerateRequest;
import gnu.capstone.G_Learn_E.global.fastapi.dto.response.*;
import gnu.capstone.G_Learn_E.global.fastapi.enums.PeriodType;
import gnu.capstone.G_Learn_E.global.fastapi.service.FastApiService;
import gnu.capstone.G_Learn_E.global.template.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/fastapi")
@RequiredArgsConstructor
public class FastApiController {

    private final FastApiService fastApiService;
    private final ProblemRepository problemRepository;

    @PostMapping("/test/extract-keywords")
    public ApiResponse<ExtractKeywordsResponse> testExtractKeywords() {
        List<Problem> problems = problemRepository.findRandomProblems(5);
        int topN = 3;

        ExtractKeywordsResponse response = fastApiService.extractKeywordsFromProblems(problems, topN);
        log.info("FastAPI Request success");
        return new ApiResponse<>(HttpStatus.OK, "키워드 추출에 성공하였습니다.", response);
    }


    @GetMapping("/test/make-problem")
    public ApiResponse<ProblemGenerateResponse> testMakeProblem() {
        FastApiProblemGenerateRequest requestDto = FastApiProblemGenerateRequest.of(
                "스프링부트 프레임워크에 대한 모든것",
                "하",
                QuestionTypes.of(
                        QuestionTypes.MultipleChoice.of(true, 1, 4),
                        QuestionTypes.Ox.of(true, 1),
                        QuestionTypes.FillInTheBlank.of(true, 1),
                        QuestionTypes.Descriptive.of(true, 1)
                )
        );
        ProblemGenerateResponse problemGenerateResponse = fastApiService.generateProblems(requestDto);
        log.info("FastAPI Request success");
        log.info("FastAPI response: {}", problemGenerateResponse);
        return new ApiResponse<>(HttpStatus.OK, "문제 생성에 성공하였습니다.", problemGenerateResponse);
    }


    @GetMapping("/test/grade-descriptive")
    public ApiResponse<GradeDescriptiveResponse> testGradeDescriptive() {
        GradeDescriptiveRequest testRequest = GradeDescriptiveRequest.of(
                List.of(
                        GradeDescriptiveRequest.Problem.of(
                                1L,
                                "시험 정리본의 필요성과 그 활용 방법에 대해 서술하시오.",
                                "시험 정리본은 학습 내용을 체계적으로 정리하여 복습을 용이하게 하고, 시험 준비에 필수적입니다. 활용 방법으로는 각 과목별 요약, 연습 문제 풀이, 개인 점검 리스트 작성 등이 있습니다.",
                                "학습 내용을 정리해서 복습하기 좋다. 활용 방법으로는 요약, 연습 문제 풀이, 시험 전 빠른 확인 등이 있다."
                        ),
                        GradeDescriptiveRequest.Problem.of(
                                2L,
                                "효율적인 시험 준비를 위한 전략을 설명하시오.",
                                "효율적인 시험 준비를 위해서는 적절한 시간 관리, 정리본 작성, 반복 학습 등이 필요합니다. 또한, 목표를 설정하고 실천하는 것이 효과적입니다.",
                                "효율적인 시험 준비를 위해서는 적절한 시간 관리, 정리본 작성, 반복 학습 등이 필요합니다. 또한, 목표를 설정하고 실천하는 것이 효과적입니다."
                        )
                )
        );
        GradeDescriptiveResponse response = fastApiService.gradeDescriptive(testRequest);
        log.info("FastAPI Request success");
        return new ApiResponse<>(HttpStatus.OK, "서술형 채점에 성공하였습니다.", response);
    }

    @GetMapping("/test/grade-blank")
    public ApiResponse<GradeBlankResponse> testGradeBlank() {
        GradeBlankRequest requestDto = GradeBlankRequest.of(
                List.of(
                        GradeBlankRequest.Problem.of(
                                1L,
                                "알고리즘 분석은 $BLANK$ 표기법을 사용하여 시간 복잡도를 나타낸다.",
                                List.of("빅오"),
                                List.of("빅 O 노테이션")
                        ),
                        GradeBlankRequest.Problem.of(
                                2L,
                                "데이터베이스는 $BLANK$ 모델을 기반으로 데이터를 저장하고 관리한다.",
                                List.of("관계형"),
                                List.of("관계형")
                        ),
                        GradeBlankRequest.Problem.of(
                                3L,
                                "운영체제는 하드웨어 자원을 효율적으로 $BLANK$하여 시스템 성능을 극대화한다.",
                                List.of("스케줄링"),
                                List.of("관리")
                        ),
                        GradeBlankRequest.Problem.of(
                                4L,
                                "네트워크 프로토콜은 OSI 모델의 $BLANK$ 계층에서 데이터 전송 규칙을 정의하고, $BLANK$ 계층에서 데이터의 포맷과 인터페이스를 담당한다.",
                                List.of("전송", "응용"),
                                List.of("transport", "application")
                        )
                )
        );
        GradeBlankResponse result = fastApiService.gradeBlank(requestDto);
        log.info("FastAPI Request success");
        return new ApiResponse<>(HttpStatus.OK, "문제 생성에 성공하였습니다.", result);
    }


    @GetMapping("/log/token-usage/{periodType}")
    public ApiResponse<TokenUsageResponse> getTokenStatus(@PathVariable(name = "periodType") String periodType) {
        PeriodType period = PeriodType.valueOf(periodType.toUpperCase());
        TokenUsageResponse response = fastApiService.getTokenUsage(period);
        return new ApiResponse<>(HttpStatus.OK, "토큰 사용량 조회에 성공하였습니다.", response);
    }

    @GetMapping("/log/api-logs/{periodType}")
    public ApiResponse<ApiLogResponse> getApiLogs(@PathVariable(name = "periodType") String periodType) {
        PeriodType period = PeriodType.valueOf(periodType.toUpperCase());
        ApiLogResponse response = fastApiService.getApiLogs(period);
        return new ApiResponse<>(HttpStatus.OK, "API 로그 조회에 성공하였습니다.", response);
    }
}
