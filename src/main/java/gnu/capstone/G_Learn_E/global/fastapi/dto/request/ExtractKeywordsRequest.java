package gnu.capstone.G_Learn_E.global.fastapi.dto.request;


import gnu.capstone.G_Learn_E.domain.problem.entity.Problem;
import gnu.capstone.G_Learn_E.global.common.serialization.Option;

import java.util.List;

public record ExtractKeywordsRequest(
    List<ProblemDTO> problems,
    int topN
) {

    public static ExtractKeywordsRequest from(List<Problem> problems, int topN) {
        List<ProblemDTO> problemDTOs = problems.stream()
                .map(ProblemDTO::from)
                .toList();
        return new ExtractKeywordsRequest(problemDTOs, topN);
    }


    private record ProblemDTO(
            Long id,
            String title,
            List<OptionDTO> options,
            List<String> answers,
            String explanation,
            String type
    ) {
        public static ProblemDTO from(Problem problem) {
            List<OptionDTO> options = (problem.getOptions()==null)
                    ? List.of()
                    : problem.getOptions().stream()
                    .map(OptionDTO::from).toList();
            return new ProblemDTO(
                    problem.getId(),
                    problem.getTitle(),
                    options,
                    problem.getAnswers(),
                    problem.getExplanation(),
                    problem.getType().toString()
            );
        }
    }

    private record OptionDTO(
            Short number,
            String content
    ) {
        public static OptionDTO from(Option option) {
            return new OptionDTO(option.getNumber(), option.getContent());
        }
    }
}
