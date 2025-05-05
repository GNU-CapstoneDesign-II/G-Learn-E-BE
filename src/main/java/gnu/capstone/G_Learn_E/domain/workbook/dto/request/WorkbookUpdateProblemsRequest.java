package gnu.capstone.G_Learn_E.domain.workbook.dto.request;

import gnu.capstone.G_Learn_E.domain.problem.dto.request.ProblemRequest;

import java.util.List;

public record WorkbookUpdateProblemsRequest(
        List<ProblemRequest> problems // 문제 리스트
) {
}
