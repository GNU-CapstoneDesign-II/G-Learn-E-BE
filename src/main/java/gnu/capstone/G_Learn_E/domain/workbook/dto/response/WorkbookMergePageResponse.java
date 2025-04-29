package gnu.capstone.G_Learn_E.domain.workbook.dto.response;

import gnu.capstone.G_Learn_E.domain.problem.dto.response.ProblemResponse;
import gnu.capstone.G_Learn_E.domain.problem.entity.Problem;
import gnu.capstone.G_Learn_E.global.common.serialization.Option;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public record WorkbookMergePageResponse(
        List<ProblemResponse> problems // 문제 리스트
) {
    public static WorkbookMergePageResponse from(List<Problem> problems) {
        AtomicInteger problemNumber = new AtomicInteger(1);
        List<ProblemResponse> list = problems.stream()
                .map(p -> {
                    // 반드시 new 키워드로 생성자 호출
                    return new ProblemResponse(
                            p.getId(),
                            problemNumber.getAndIncrement(),
                            p.getType().name(),
                            p.getTitle(),
                            p.getOptions() == null
                                    ? List.of()
                                    : p.getOptions().stream()
                                    .map(Option::getContent)
                                    .toList(),
                            p.getAnswers(),
                            p.getExplanation()
                    );
                })
                .toList();
        return new WorkbookMergePageResponse(list);
    }
}
