package gnu.capstone.G_Learn_E.global.fastapi.dto.request;

import gnu.capstone.G_Learn_E.domain.workbook.dto.request.QuestionTypes;

public record FastApiProblemGenerateRequest(
        String content,
        String difficulty,
        QuestionTypes questionTypes
) {
    public static FastApiProblemGenerateRequest of(String content, String difficulty, QuestionTypes questionTypes) {
        return new FastApiProblemGenerateRequest(content, difficulty, questionTypes);
    }
}
