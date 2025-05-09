package gnu.capstone.G_Learn_E.domain.workbook.dto.request;

public record WorkbookUpdateRequest(
        String name,
        String professor,
        String examType,
        Integer coverImage,
        Integer courseYear,
        String semester
) {
}
