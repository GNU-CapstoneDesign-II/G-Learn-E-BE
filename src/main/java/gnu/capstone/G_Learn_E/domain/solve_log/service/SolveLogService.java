package gnu.capstone.G_Learn_E.domain.solve_log.service;

import gnu.capstone.G_Learn_E.domain.solve_log.repository.SolveLogRepository;
import gnu.capstone.G_Learn_E.domain.solve_log.repository.SolvedWorkbookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SolveLogService {

    private final SolveLogRepository solveLogRepository;
    private final SolvedWorkbookRepository solvedWorkbookRepository;

    // TODO : 풀이 로그 서비스 구현
}
