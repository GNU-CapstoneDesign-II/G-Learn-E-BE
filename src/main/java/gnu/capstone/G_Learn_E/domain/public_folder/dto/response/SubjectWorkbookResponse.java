package gnu.capstone.G_Learn_E.domain.public_folder.dto.response;

import gnu.capstone.G_Learn_E.global.common.dto.response.PublicWorkbook;

import java.util.List;

public record SubjectWorkbookResponse(
        List<PublicWorkbook> workbooks
) {
}