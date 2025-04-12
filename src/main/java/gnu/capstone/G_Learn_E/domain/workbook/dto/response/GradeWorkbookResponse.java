package gnu.capstone.G_Learn_E.domain.workbook.dto.response;

public record GradeWorkbookResponse(
        int correctCount,
        int wrongCount
) {
    public static GradeWorkbookResponse of(int correctCount, int wrongCount) {
        return new GradeWorkbookResponse(correctCount, wrongCount);
    }
}
