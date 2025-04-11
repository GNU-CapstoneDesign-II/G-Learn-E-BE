package gnu.capstone.G_Learn_E.domain.workbook.controller;

import gnu.capstone.G_Learn_E.domain.user.entity.User;
import gnu.capstone.G_Learn_E.domain.workbook.dto.request.ProblemGenerateRequest;
import gnu.capstone.G_Learn_E.domain.workbook.dto.response.WorkbookResponse;
import gnu.capstone.G_Learn_E.domain.workbook.entity.Workbook;
import gnu.capstone.G_Learn_E.domain.workbook.service.WorkbookService;
import gnu.capstone.G_Learn_E.domain.workbook.dto.response.ProblemGenerateResponse;
import gnu.capstone.G_Learn_E.global.fastapi.service.FastApiService;
import gnu.capstone.G_Learn_E.global.template.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/workbook")
@RequiredArgsConstructor
public class WorkbookController {

    private final WorkbookService workbookService;
    private final FastApiService fastApiService;

    // TODO : 문제집 컨트롤러 구현


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, path = "/generate")
    public ApiResponse<WorkbookResponse> createWorkbook(
            @AuthenticationPrincipal User user,
            @ModelAttribute ProblemGenerateRequest request
    ) {


        ProblemGenerateResponse problemGenerateResponse = fastApiService.makeDummyResponse1(request);
        log.info("Response : {}", problemGenerateResponse);

        Workbook workbook = workbookService.createWorkbook(problemGenerateResponse, user);

        WorkbookResponse response = WorkbookResponse.of(workbook);

        // Workbook 생성 로직 처리 후 결과 반환
        return new ApiResponse<>(HttpStatus.OK, "문제집 생성에 성공하였습니다.", response);
    }
}
