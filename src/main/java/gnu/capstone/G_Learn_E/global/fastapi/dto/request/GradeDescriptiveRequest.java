package gnu.capstone.G_Learn_E.global.fastapi.dto.request;

import java.util.List;

public record GradeDescriptiveRequest(
        List<Problem> items
) {
    public static GradeDescriptiveRequest of(List<Problem> items) {
        return new GradeDescriptiveRequest(items);
    }

    public record Problem(
            Long id,
            String question,
            String answer,
            String input
    ) {
        public static Problem of(Long id, String quesstion, String answer, String input) {
            return new Problem(id, quesstion, answer, input);
        }
    }
}
