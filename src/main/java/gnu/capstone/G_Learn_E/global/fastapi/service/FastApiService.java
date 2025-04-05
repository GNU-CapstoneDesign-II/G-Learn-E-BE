package gnu.capstone.G_Learn_E.global.fastapi.service;

import gnu.capstone.G_Learn_E.global.fastapi.dto.request.ProblemGenerateRequest;
import gnu.capstone.G_Learn_E.global.fastapi.dto.response.ProblemGenerateResponse;
import gnu.capstone.G_Learn_E.global.fastapi.entity.FastApiProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FastApiService {

    private final RestTemplate restTemplate;
    private final FastApiProperties fastApiProperties;


    public ProblemGenerateResponse generateProblems(ProblemGenerateRequest requestDto) {
        FastApiProperties.Endpoint endpoint = getEndpoint("create-problem");
        String url = fastApiProperties.baseUrl() + endpoint.path();

        // FastAPI 서버로 POST 요청
        ResponseEntity<ProblemGenerateResponse> response = restTemplate.postForEntity(url, requestDto, ProblemGenerateResponse.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("FastAPI response: {}", response.getBody());
            return response.getBody();
        } else {
            log.error("Failed to call FastAPI: {}", response.getStatusCode());
            throw new RuntimeException("Failed to call FastAPI");
        }
    }



    private FastApiProperties.Endpoint getEndpoint(String endpointName) {
        return fastApiProperties.endpoints().get(endpointName);
    }


    private ProblemGenerateResponse makeDummyResponse() {
        // Dummy Data
        return ProblemGenerateResponse.of(
                ProblemGenerateResponse.Result.of(
                        List.of(
                                ProblemGenerateResponse.MultipleChoice.of(
                                        "스프링부트의 특징은 무엇인가요?",
                                        List.of("경량화", "대규모 애플리케이션 지원", "모듈화", "모든 언어 지원"),
                                        "1",
                                        "스프링부트는 경량화된 프레임워크로, 대규모 애플리케이션을 지원합니다."
                                )
                        ),
                        List.of(
                                ProblemGenerateResponse.Ox.of(
                                        "스프링부트는 경량화된 프레임워크이다.",
                                        "O",
                                        "스프링부트는 경량화된 프레임워크입니다."
                                )
                        ),
                        List.of(
                                ProblemGenerateResponse.FillInTheBlank.of(
                                        "스프링부트는 _____ 프레임워크이다.",
                                        List.of("경량화된"),
                                        "스프링부트는 경량화된 프레임워크입니다."
                                )
                        ),
                        List.of(
                                ProblemGenerateResponse.Descriptive.of(
                                        "스프링부트의 특징을 설명하시오.",
                                        "스프링부트는 경량화된 프레임워크로, 대규모 애플리케이션을 지원합니다."
                                )
                        )
                )
        );
    }
}
