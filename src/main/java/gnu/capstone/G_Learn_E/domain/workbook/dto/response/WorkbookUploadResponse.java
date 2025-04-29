package gnu.capstone.G_Learn_E.domain.workbook.dto.response;

public record WorkbookUploadResponse(
        Long id,
        Subject subject
) {
    public static WorkbookUploadResponse of(
            Long id,
            Long subjectId,
            String subjectName
    ) {
        return new WorkbookUploadResponse(
                id,
                Subject.of(subjectId, subjectName)
                );
    }
    record Subject(
            Long id,
            String name
    ) {
        public static Subject of(Long id, String name) {
            return new Subject(id, name);
        }
    }
}
