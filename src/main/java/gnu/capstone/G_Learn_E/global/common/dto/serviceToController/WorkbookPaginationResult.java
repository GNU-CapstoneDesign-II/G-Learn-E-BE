package gnu.capstone.G_Learn_E.global.common.dto.serviceToController;

import gnu.capstone.G_Learn_E.domain.workbook.entity.Workbook;
import gnu.capstone.G_Learn_E.global.common.dto.response.PageInfo;

import java.util.List;

public record WorkbookPaginationResult(
        PageInfo pageInfo,
        List<Workbook> workbookList
) {
    public static WorkbookPaginationResult from(
            PageInfo pageInfo,
            List<Workbook> workbookList
    ) {
        return new WorkbookPaginationResult(pageInfo, workbookList);
    }
}
