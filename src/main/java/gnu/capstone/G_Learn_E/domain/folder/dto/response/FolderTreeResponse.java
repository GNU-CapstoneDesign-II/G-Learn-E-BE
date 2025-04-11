package gnu.capstone.G_Learn_E.domain.folder.dto.response;

import gnu.capstone.G_Learn_E.domain.folder.entity.Folder;

import java.util.List;

public record FolderTreeResponse(
        Long id,
        String name,
        List<FolderTreeResponse> childFolders
) {
    public static FolderTreeResponse of(Long id, String name, List<FolderTreeResponse> childFolders) {
        return new FolderTreeResponse(
                id,
                name,
                childFolders
        );
    }

    public static FolderTreeResponse from(Folder folder) {
        return new FolderTreeResponse(
                folder.getId(),
                folder.getName(),
                folder.getChildren().stream().map(FolderTreeResponse::from).toList()
        );
    }
}
