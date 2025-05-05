package gnu.capstone.G_Learn_E.global.fastapi.dto.request;

import java.util.List;

public record GradeBlankRequest(
        List<Problem> items
) {
    public static GradeBlankRequest of(List<Problem> items) {
        return new GradeBlankRequest(items);
    }

    public record Problem(
            Long id,
            String question,
            List<String> answer,
            List<String> input
    ) {
        public static Problem of(Long id, String question, List<String> answer, List<String> input) {
            return new Problem(id, question, answer, input);
        }
    }
}
