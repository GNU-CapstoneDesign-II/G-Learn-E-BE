package gnu.capstone.G_Learn_E.domain.public_folder.dto.response;

import java.util.List;

public record SubjectWorkbookResponse(
        List<WorkbookResponse> workbooks
) {
}