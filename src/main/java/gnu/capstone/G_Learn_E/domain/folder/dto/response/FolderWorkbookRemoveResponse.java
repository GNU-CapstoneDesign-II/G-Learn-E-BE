package gnu.capstone.G_Learn_E.domain.folder.dto.response;

public record FolderWorkbookRemoveResponse(
        boolean isUploaded
) {
    public static FolderWorkbookRemoveResponse of(boolean isUploaded) {
        return new FolderWorkbookRemoveResponse(isUploaded);
    }
}
