package gnu.capstone.G_Learn_E.global.fastapi.dto.response;

import java.util.List;

public record ProblemGenerateResponse(
        Result result
) {
    public static ProblemGenerateResponse of(Result result) {
        return new ProblemGenerateResponse(result);
    }
    public record Result(
            List<MultipleChoice> multipleChoice,
            List<Ox> ox,
            List<FillInTheBlank> fillInTheBlank,
            List<Descriptive> descriptive
    ) {
        public static Result of(List<MultipleChoice> multipleChoice, List<Ox> ox, List<FillInTheBlank> fillInTheBlank, List<Descriptive> descriptive) {
            return new Result(multipleChoice, ox, fillInTheBlank, descriptive);
        }
    }

    public record MultipleChoice(
            String question,
            List<String> options,
            String answer,
            String explanation
    ) {
        public static MultipleChoice of(String question, List<String> options, String answer, String explanation) {
            return new MultipleChoice(question, options, answer, explanation);
        }
    }

    public record Ox(
            String question,
            String answer,
            String explanation
    ) {
        public static Ox of(String question, String answer, String explanation) {
            return new Ox(question, answer, explanation);
        }
    }

    public record FillInTheBlank(
            String question,
            List<String> answer,
            String explanation
    ) {
        public static FillInTheBlank of(String question, List<String> answer, String explanation) {
            return new FillInTheBlank(question, answer, explanation);
        }
    }

    public record Descriptive(
            String question,
            String answer
    ) {
        public static Descriptive of(String question, String answer) {
            return new Descriptive(question, answer);
        }
    }
}
