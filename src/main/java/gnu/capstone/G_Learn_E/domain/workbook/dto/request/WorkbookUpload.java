package gnu.capstone.G_Learn_E.domain.workbook.dto.request;

public record WorkbookUpload(
        Long id,
        Long collegeId,
        Long departmentId,
        Long subjectId
) {
}
