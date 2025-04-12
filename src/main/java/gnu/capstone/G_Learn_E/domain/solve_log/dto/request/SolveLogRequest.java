package gnu.capstone.G_Learn_E.domain.solve_log.dto.request;

import java.util.List;

public record SolveLogRequest(
        Long problemId,
        List<String> submitAnswer
) {
}
