package gnu.capstone.G_Learn_E.domain.problem.controller;

import gnu.capstone.G_Learn_E.domain.problem.service.ProblemService;
import gnu.capstone.G_Learn_E.domain.solve_log.service.SolveLogService;
import gnu.capstone.G_Learn_E.domain.workbook.service.WorkbookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/problem")
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;
    private final WorkbookService workbookService;
    private final SolveLogService solveLogService;


    /// TODO : 문제 컨트롤러 구현
}
