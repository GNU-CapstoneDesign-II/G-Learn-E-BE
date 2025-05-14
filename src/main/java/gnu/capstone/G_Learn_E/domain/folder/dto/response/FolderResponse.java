package gnu.capstone.G_Learn_E.domain.folder.dto.response;

import gnu.capstone.G_Learn_E.domain.folder.entity.Folder;
import gnu.capstone.G_Learn_E.domain.workbook.entity.Workbook;
import gnu.capstone.G_Learn_E.global.common.dto.response.PrivateWorkbook;

import java.util.List;

public record FolderResponse(
        Long id,
        String name,
        Long parentId,
        String createdAt,
        List<ChildFolderResponse> childFolders,
        List<PrivateWorkbook> childWorkbooks
) {
    public static FolderResponse of(Long id, String name, Long parentId, String createdAt,
                                    List<Folder> childFolders, List<Workbook> childWorkbooks) {
        return new FolderResponse(
                id,
                name,
                parentId,
                createdAt,
                childFolders.stream().map(ChildFolderResponse::of).toList(),
                childWorkbooks.stream().map(PrivateWorkbook::from).toList()
        );
    }

    public record ChildFolderResponse(
            Long id,
            String name,
            String createdAt
    ) {
        public static ChildFolderResponse of(Long id, String name, String createdAt) {
            return new ChildFolderResponse(
                    id,
                    name,
                    createdAt
            );
        }
        public static ChildFolderResponse of(Folder folder) {
            return new ChildFolderResponse(
                    folder.getId(),
                    folder.getName(),
                    folder.getCreatedAt().toString()
            );
        }
    }
}
