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
            ResponseEntity<ToStringResponse> response = restTemplate.postForEntity(url, requestEntity, ToStringResponse.class);
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

    public String audioToStringRequest(AudioToStringRequest request) {
        try {
            // 1) 엔드포인트 조회
            FastApiProperties.Endpoint endpoint = getEndpoint("audio-to-string");
            String url = fastApiProperties.baseUrl() + endpoint.path();

            // 2) MultipartFile 추출
            MultipartFile audioFile = request.audioFile();

            // 3) 바디 구성 (InputStreamResource 래핑)
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add(
                    "audioFile",
                    new MultipartInputStreamFileResource(
                            audioFile.getInputStream(),
                            audioFile.getOriginalFilename()
                    )
            );

            // 4) 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> requestEntity =
                    new HttpEntity<>(body, headers);

            // 5) API 호출
            log.info("FastAPI URL: {}", url);
            ResponseEntity<ToStringResponse> response =
                    restTemplate.postForEntity(url, requestEntity, ToStringResponse.class);

            // 6) 응답 처리
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody().text();
            } else {
                throw new RuntimeException("FastAPI 호출 실패: " + response.getStatusCode());
            }
        } catch (IOException e) {
            log.error("오디오 파일 읽기 오류: {}", e.getMessage());
            throw new RuntimeException("Audio to String 변환 실패", e);
        }
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



    public ProblemGenerateResponse makeDummyResponse(ProblemGenerateRequest request) {
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
                                        "스프링부트는 [[$BLANK$]] 프레임워크이다.",
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

    public ProblemGenerateResponse makeDummyResponse1(ProblemGenerateRequest request) {
        return ProblemGenerateResponse.of(
                ProblemGenerateResponse.Result.of(
                        // 객관식 문제 (MultipleChoice)
                        List.of(
                                ProblemGenerateResponse.MultipleChoice.of(
                                        "변환(Transform)의 종류가 아닌 것은 무엇인가?",
                                        List.of("이동(Translation)", "축소확대(Scaling)", "회전(Rotation)", "왜곡(Deformation)"),
                                        "4",
                                        "변환의 세 가지 주요 종류는 이동(Translation), 축소확대(Scaling), 회전(Rotation)입니다. 왜곡(Deformation)은 제시된 변환의 종류에 해당하지 않습니다."
                                ),
                                ProblemGenerateResponse.MultipleChoice.of(
                                        "Affine 변환의 정의에 포함되지 않는 것은 무엇인가?",
                                        List.of("Linear transform", "Translation", "Rotation", "스케일링(Skew)"),
                                        "4",
                                        "Affine 변환은 선형 변환(Linear transform)과 평행 이동(Translation)을 포함하지만, 스케일링(Skew)은 여기에 포함되지 않습니다."
                                )
                        ),
                        // OX 문제 (Ox)
                        List.of(
                                ProblemGenerateResponse.Ox.of(
                                        "모든 물체는Affine Transform을 통해 선형 변환과 평행 이동을 동시에 수행할 수 있다.",
                                        "O",
                                        "Affine Transform은 선형 변환(Linear transform)과 평행 이동(Translation)을 포함하여 변환을 수행할 수 있습니다."
                                ),
                                ProblemGenerateResponse.Ox.of(
                                        "3D 회전에서 z축을 중심으로 회전할 때, y좌표는 변화하지 않을 것이다.",
                                        "O",
                                        "z축을 중심으로 회전할 때, x와 y좌표는 2차원 회전의 결과와 같고 z좌표는 변화하지 않습니다."
                                )
                        ),
                        // 빈칸 채우기 문제 (FillInTheBlank)
                        List.of(
                                ProblemGenerateResponse.FillInTheBlank.of(
                                        "변환의 범주에는 [[$BLANK$]], [[$BLANK$]], [[$BLANK$]]이 포함됩니다.",
                                        List.of("Translation", "Rotation", "Scaling"),
                                        "변환에는 이동(Translation), 회전(Rotation), 축소확대(Scaling)의 세 가지 주요 종류가 있습니다."
                                ),
                                ProblemGenerateResponse.FillInTheBlank.of(
                                        "역변환을 통해 물체의 방향을 복원하기 위해서는 [[$BLANK$]] 행렬을 적용해야 합니다.",
                                        List.of("역행렬"),
                                        "물체의 특정 방향으로 회전되었을 때 원래의 방향으로 돌아가기 위해서는 역행렬을 적용해야 합니다."
                                )
                        ),
                        // 서술형 문제 (Descriptive)
                        List.of(
                                ProblemGenerateResponse.Descriptive.of(
                                        "Affine Transform의 개념을 설명하시오.",
                                        "Affine Transform은 선형 변환(Linear transform)과 평행 이동(Translation)을 포함한 변환으로, 이들은 행렬 형태로 표현되어 여러 변환을 동시에 수행할 수 있습니다."
                                ),
                                ProblemGenerateResponse.Descriptive.of(
                                        "3D Scaling의 정의와 이를 어떻게 적용하는지 설명하시오.",
                                        "3D Scaling은 물체의 크기를 조정하는 변환으로, 각 정점에 동일한 Scaling Matrix를 적용하여 이루어집니다. 유니폼 Scaling은 모든 방향에 동일한 Scaling Factor를 사용하고, Non-uniform Scaling은 각 방향에 따라 다른 Scaling Factor를 적용합니다."
                                )
                        )
                )
        );
    }

}
