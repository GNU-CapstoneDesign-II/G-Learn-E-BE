package gnu.capstone.G_Learn_E.domain.solve_log.controller;

import gnu.capstone.G_Learn_E.domain.solve_log.service.SolveLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/solve-log")
@RequiredArgsConstructor
public class SolveLogController {

    private final SolveLogService solveLogService;

    // TODO : 풀이 로그 컨트롤러 구현

}
