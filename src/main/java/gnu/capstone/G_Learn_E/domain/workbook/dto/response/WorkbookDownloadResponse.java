package gnu.capstone.G_Learn_E.domain.workbook.dto.response;

public record WorkbookDownloadResponse(
        Long id
) {
    public static WorkbookDownloadResponse of(
            Long id
    ) {
        return new WorkbookDownloadResponse(id);
    }
}
