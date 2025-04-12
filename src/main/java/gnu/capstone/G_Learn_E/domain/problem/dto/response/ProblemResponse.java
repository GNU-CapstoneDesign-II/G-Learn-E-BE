package gnu.capstone.G_Learn_E.domain.problem.dto.response;

import gnu.capstone.G_Learn_E.domain.problem.entity.Problem;
import gnu.capstone.G_Learn_E.global.common.serialization.Option;

import java.util.List;

public record ProblemResponse(
        Long id, // 문제 ID
        Integer problemNumber, // 문제 번호
        String type, // 문제 유형
        String title, // 문제 제목
        List<String> options, // 문제 선택지 (객관식 문제에만 해당)

        List<String> answers, // 정답 리스트 (빈칸 복수, 나머지 단일)
        String explanation // 해설
) {
    public static ProblemResponse from(
            Long id,
            Integer problemNumber,
            String type,
            String title,
            List<String> options,
            List<String> answers,
            String explanation
    ) {
        return new ProblemResponse(id, problemNumber, type, title, options, answers, explanation);
    }

    public static ProblemResponse from(Problem problem) {
        return from(
                problem.getId(),
                problem.getProblemNumber(),
                problem.getType().name(),
                problem.getTitle(),
                problem.getOptions() != null ? problem.getOptions().stream().map(Option::getContent).toList() : null,
                problem.getAnswers(),
                problem.getExplanation()
        );
    }

    public static List<ProblemResponse> from(List<Problem> problems) {
        return problems.stream()
                .map(ProblemResponse::from)
                .toList();
    }
}
