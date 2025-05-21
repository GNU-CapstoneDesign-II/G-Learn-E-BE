package gnu.capstone.G_Learn_E.domain.workbook.dto.request;

import gnu.capstone.G_Learn_E.domain.workbook.enums.ExamType;
import gnu.capstone.G_Learn_E.domain.workbook.enums.Semester;

public record WorkbookUpdateRequest(
        String name,
        String professor,
        String examType,
        Integer courseYear,
        String semester
) {
}
