package gnu.capstone.G_Learn_E.domain.folder.dto.request;

public record CreateFolderRequest(
        String name,
        Long parentId
) {
}
