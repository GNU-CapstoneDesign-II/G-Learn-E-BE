package gnu.capstone.G_Learn_E.domain.workbook.dto.request;

public record ProblemGenerateRequest(
        Content content,
        String difficulty,
        QuestionTypes questionTypes
) {
    public static ProblemGenerateRequest of(Content content, String difficulty, QuestionTypes questionTypes) {
        return new ProblemGenerateRequest(content, difficulty, questionTypes);
    }
}
