package gnu.capstone.G_Learn_E.global.fastapi.controller;

import gnu.capstone.G_Learn_E.global.fastapi.dto.request.ProblemGenerateRequest;
import gnu.capstone.G_Learn_E.global.fastapi.dto.response.ProblemGenerateResponse;
import gnu.capstone.G_Learn_E.global.fastapi.service.FastApiService;
import gnu.capstone.G_Learn_E.global.template.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/fastapi")
@RequiredArgsConstructor
public class FastApiController {

    private final FastApiService fastApiService;


    @GetMapping("/test")
    public ApiResponse<ProblemGenerateResponse> test() {
        log.info("FastAPI test endpoint called");
        ProblemGenerateRequest requestDto = ProblemGenerateRequest.of(
                "스프링부트 프레임워크에 대한 모든것",
                "하",
                ProblemGenerateRequest.QuestionTypes.of(
                        ProblemGenerateRequest.MultipleChoice.of(true, 1, 4),
                        ProblemGenerateRequest.Ox.of(true, 1),
                        ProblemGenerateRequest.FillInTheBlank.of(true, 1),
                        ProblemGenerateRequest.Descriptive.of(true, 1)
                )
        );
        ProblemGenerateResponse problemGenerateResponse = fastApiService.generateProblems(requestDto);
        log.info("FastAPI Request success");
        log.info("FastAPI response: {}", problemGenerateResponse);
        return new ApiResponse<>(HttpStatus.OK, "FastAPI test success", problemGenerateResponse);
    }
}
