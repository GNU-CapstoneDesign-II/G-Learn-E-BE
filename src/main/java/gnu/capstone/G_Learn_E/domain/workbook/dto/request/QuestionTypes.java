package gnu.capstone.G_Learn_E.domain.workbook.dto.request;

public record QuestionTypes(
        MultipleChoice multipleChoice,
        Ox ox,
        FillInTheBlank fillInTheBlank,
        Descriptive descriptive
) {
    public static QuestionTypes of(MultipleChoice multipleChoice, Ox ox, FillInTheBlank fillInTheBlank, Descriptive descriptive) {
        return new QuestionTypes(multipleChoice, ox, fillInTheBlank, descriptive);
    }

    public record MultipleChoice(
            boolean enable,
            int numQuestions,
            int numOptions
    ) {
        public static MultipleChoice of(boolean enabled, int numQuestions, int numOptions) {
            return new MultipleChoice(enabled, numQuestions, numOptions);
        }
    }

    public record Ox(
            boolean enable,
            int numQuestions
    ) {
        public static Ox of(boolean enabled, int numQuestions) {
            return new Ox(enabled, numQuestions);
        }
    }

    public record FillInTheBlank(
            boolean enable,
            int numQuestions
    ) {
        public static FillInTheBlank of(boolean enabled, int numQuestions) {
            return new FillInTheBlank(enabled, numQuestions);
        }
    }

    public record Descriptive(
            boolean enable,
            int numQuestions
    ) {
        public static Descriptive of(boolean enabled, int numQuestions) {
            return new Descriptive(enabled, numQuestions);
        }
    }
}

