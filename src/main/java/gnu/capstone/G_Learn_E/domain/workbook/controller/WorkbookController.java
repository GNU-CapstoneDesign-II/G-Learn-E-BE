package gnu.capstone.G_Learn_E.domain.workbook.controller;

import gnu.capstone.G_Learn_E.domain.workbook.service.WorkbookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/workbook")
@RequiredArgsConstructor
public class WorkbookController {

    private final WorkbookService workbookService;

    // TODO : 문제집 컨트롤러 구현
}
