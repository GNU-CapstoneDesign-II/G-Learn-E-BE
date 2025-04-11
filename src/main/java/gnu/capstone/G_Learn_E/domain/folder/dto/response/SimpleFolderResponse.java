package gnu.capstone.G_Learn_E.domain.folder.dto.response;

import gnu.capstone.G_Learn_E.domain.folder.entity.Folder;

public record SimpleFolderResponse(
        Long id,
        String name,
        Long parentId,
        String createdAt
) {
    public static SimpleFolderResponse of(Long id, String name, Long parentId, String createdAt) {
        return new SimpleFolderResponse(
                id,
                name,
                parentId,
                createdAt
        );
    }

    public static SimpleFolderResponse from(Folder folder) {
        return new SimpleFolderResponse(
                folder.getId(),
                folder.getName(),
                folder.getParent() != null ? folder.getParent().getId() : null,
                folder.getCreatedAt().toString()
        );
    }
}
