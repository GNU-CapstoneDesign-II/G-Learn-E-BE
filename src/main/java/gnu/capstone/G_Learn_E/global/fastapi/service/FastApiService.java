package gnu.capstone.G_Learn_E.global.fastapi.service;

import gnu.capstone.G_Learn_E.domain.workbook.dto.request.ProblemGenerateRequest;
import gnu.capstone.G_Learn_E.domain.workbook.dto.response.ProblemGenerateResponse;
import gnu.capstone.G_Learn_E.global.fastapi.dto.request.*;
import gnu.capstone.G_Learn_E.global.fastapi.dto.response.*;
import gnu.capstone.G_Learn_E.global.fastapi.entity.FastApiProperties;
import gnu.capstone.G_Learn_E.global.fastapi.enums.PeriodType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FastApiService {

    private final RestTemplate restTemplate;
    private final FastApiProperties fastApiProperties;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    private FastApiProperties.Endpoint getEndpoint(String endpointName) {
        return fastApiProperties.endpoints().get(endpointName);
    }


    public ProblemGenerateResponse generateProblems(FastApiProblemGenerateRequest orgRequest) {
        FastApiProperties.Endpoint endpoint = getEndpoint("create-problem");
        String url = fastApiProperties.baseUrl() + endpoint.path();

        FastApiProblemGenerateRequest request = FastApiProblemGenerateRequest.of(
                orgRequest.content(),
                orgRequest.difficulty(),
                orgRequest.questionTypes()
        );
        // FastAPI 서버로 POST 요청
        ResponseEntity<ProblemGenerateResponse> response = restTemplate.postForEntity(url, request, ProblemGenerateResponse.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("FastAPI response: {}", response.getBody());
            return response.getBody();
        } else {
            log.error("Failed to call FastAPI: {}", response.getStatusCode());
            throw new RuntimeException("Failed to call FastAPI");
        }
    }

    public ProblemGenerateResponse generateProblems(ProblemGenerateRequest orgRequest) {
        String summaryText = orgRequest.content().summaryText();
        MultipartFile pdfFile = orgRequest.content().pdfFile();
        MultipartFile audioFile = orgRequest.content().audioFile();

        String content = "";
        if (summaryText != null) {
            log.info("summaryText: {}", summaryText);
            content += summaryText;
        }
        if (pdfFile != null) {
            String tmp = pdfToStringRequest(PdfToStringRequest.of(pdfFile));
            log.info("pdfFile: {}", tmp);
            content += tmp;
        }
        if (audioFile != null) {
            content += audioToStringRequest(AudioToStringRequest.of(audioFile));
        }
        FastApiProblemGenerateRequest request = FastApiProblemGenerateRequest.of(
                content,
                orgRequest.difficulty(),
                orgRequest.questionTypes()
        );
        return generateProblems(request);
    }

    private String pdfToStringRequest(PdfToStringRequest request){
        try {
            FastApiProperties.Endpoint endpoint = getEndpoint("pdf-to-string");
            String url = fastApiProperties.baseUrl() + endpoint.path();

            MultipartFile pdfFile = request.pdfFile();

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("pdfFile", new MultipartInputStreamFileResource(pdfFile.getInputStream(), pdfFile.getOriginalFilename()));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // FastAPI 서버에 POST 요청 보내기
            ResponseEntity<PdfToStringResponse> response = restTemplate.postForEntity(url, requestEntity, PdfToStringResponse.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Pdf To String response: " + response.getBody());
                return response.getBody().text();
            } else {
                System.err.println("Failed to call FastAPI: " + response.getStatusCode());
                throw new RuntimeException("Failed to call FastAPI");
            }
        } catch (IOException e) {
            log.error("Error reading PDF file: {}", e.getMessage());
            throw new RuntimeException("Failed to call FastAPI");
        }

    }

    private String audioToStringRequest(AudioToStringRequest request){

        return "converted";
    }



    public GradeDescriptiveResponse gradeDescriptive(GradeDescriptiveRequest request) {
        FastApiProperties.Endpoint endpoint = getEndpoint("grade-descriptive");
        String url = fastApiProperties.baseUrl() + endpoint.path();

        // FastAPI 서버로 POST 요청
        ResponseEntity<GradeDescriptiveResponse> response = restTemplate.postForEntity(url, request, GradeDescriptiveResponse.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("FastAPI response: {}", response.getBody());
            return response.getBody();
        } else {
            log.error("Failed to call FastAPI: {}", response.getStatusCode());
            throw new RuntimeException("Failed to call FastAPI");
        }
    }

    public GradeBlankResponse gradeBlank(GradeBlankRequest request) {
        FastApiProperties.Endpoint endpoint = getEndpoint("grade-blank");
        String url = fastApiProperties.baseUrl() + endpoint.path();

        // FastAPI 서버로 POST 요청
        ResponseEntity<GradeBlankResponse> response = restTemplate.postForEntity(url, request, GradeBlankResponse.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("FastAPI response: {}", response.getBody());
            return response.getBody();
        } else {
            log.error("Failed to call FastAPI: {}", response.getStatusCode());
            throw new RuntimeException("Failed to call FastAPI");
        }
    }


    public TokenUsageResponse getTokenUsage(PeriodType periodType) {

        FastApiProperties.Endpoint endpoint = null;

        if(periodType == PeriodType.DAILY) {
            endpoint = getEndpoint("daily-usage");
        } else if(periodType == PeriodType.WEEKLY) {
            endpoint = getEndpoint("weekly-usage");
        } else if(periodType == PeriodType.MONTHLY) {
            endpoint = getEndpoint("monthly-usage");
        } else {
            // TODO: 예외 처리
            throw new IllegalArgumentException("Invalid period type: " + periodType);
        }

        String date = LocalDateTime.now().format(dateTimeFormatter);
        String url = fastApiProperties.baseUrl() + endpoint.path() + "?date=" + date;

        // FastAPI 서버로 GET 요청
        ResponseEntity<TokenUsageResponse> response = restTemplate.getForEntity(url, TokenUsageResponse.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("FastAPI response: {}", response.getBody());
            return response.getBody();
        } else {
            log.error("Failed to call FastAPI: {}", response.getStatusCode());
            throw new RuntimeException("Failed to call FastAPI");
        }
    }

    public ApiLogResponse getApiLogs(PeriodType periodType) {

        FastApiProperties.Endpoint endpoint = null;
        if(periodType == PeriodType.DAILY) {
            endpoint = getEndpoint("daily-logs");
        } else if(periodType == PeriodType.WEEKLY) {
            endpoint = getEndpoint("weekly-logs");
        } else if(periodType == PeriodType.MONTHLY) {
            endpoint = getEndpoint("monthly-logs");
        } else {
            // TODO: 예외 처리
            throw new IllegalArgumentException("Invalid period type: " + periodType);
        }

        String date = LocalDateTime.now().format(dateTimeFormatter);
        String url = fastApiProperties.baseUrl() + endpoint.path() + "?date=" + date;

        // FastAPI 서버로 GET 요청
        ResponseEntity<ApiLogResponse> response = restTemplate.getForEntity(url, ApiLogResponse.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("FastAPI response: {}", response.getBody());
            return response.getBody();
        } else {
            log.error("Failed to call FastAPI: {}", response.getStatusCode());
            throw new RuntimeException("Failed to call FastAPI");
        }
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


    static class MultipartInputStreamFileResource extends InputStreamResource {

        private final String filename;

        public MultipartInputStreamFileResource(InputStream inputStream, String filename) {
            super(inputStream);
            this.filename = filename;
        }

        @Override
        public String getFilename() {
            return this.filename;
        }

        @Override
        public long contentLength() throws IOException {
            return -1; // 파일 크기를 모를 경우 -1 반환
        }
    }
}
