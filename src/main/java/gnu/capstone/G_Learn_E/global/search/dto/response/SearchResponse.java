package gnu.capstone.G_Learn_E.global.search.dto.response;

import gnu.capstone.G_Learn_E.domain.folder.dto.response.SimpleFolderResponse;
import gnu.capstone.G_Learn_E.domain.folder.entity.Folder;
import gnu.capstone.G_Learn_E.domain.user.entity.User;
import gnu.capstone.G_Learn_E.domain.workbook.entity.Workbook;
import gnu.capstone.G_Learn_E.global.common.dto.response.Author;
import gnu.capstone.G_Learn_E.global.common.dto.response.PrivateWorkbook;
import gnu.capstone.G_Learn_E.global.common.dto.response.PublicPath;
import gnu.capstone.G_Learn_E.global.common.dto.response.PublicWorkbook;

import java.util.List;
import java.util.Map;
import java.util.Set;

public record SearchResponse(
        List<SearchedPrivateWorkbook> privateWorkbooks,
        List<SearchedPublicWorkbook> publicWorkbooks
) {

    public static SearchResponse from(
            List<Workbook> privateWorkbooks,
            Map<Long, Folder> privateWorkbookPaths,
            List<Workbook> publicWorkbooks,
            Set<Long> usersDownloaded,
            Map<Long, List<PublicPath>> publicWorkbookPaths
    ) {
        return new SearchResponse(
                privateWorkbooks == null ? null :
                        privateWorkbooks.stream()
                                .map(workbook -> SearchedPrivateWorkbook.from(
                                        workbook, privateWorkbookPaths.get(workbook.getId())
                                ))
                                .toList(),
                publicWorkbooks == null ? null :
                        publicWorkbooks.stream()
                                .map(workbook -> SearchedPublicWorkbook.from(
                                        PublicWorkbook.from(
                                                workbook, usersDownloaded.contains(workbook.getId())
                                        ),
                                        workbook.getAuthor(),
                                        publicWorkbookPaths.get(workbook.getId()
                                        )))
                                .toList()
        );
    }

    private record SearchedPrivateWorkbook(
            PrivateWorkbook workbook,
            SimpleFolderResponse folder
    ) {
        private static SearchedPrivateWorkbook from(
                Workbook workbook, Folder folder
        ) {
            return new SearchedPrivateWorkbook(
                    PrivateWorkbook.from(workbook),
                    SimpleFolderResponse.from(folder)
            );
        }
    }

    private record SearchedPublicWorkbook(
            PublicWorkbook workbook,
            Author author,
            List<PublicPath> paths
    ) {
        private static SearchedPublicWorkbook from(
                PublicWorkbook workbook,
                User author,
                List<PublicPath> paths
        ) {
            return new SearchedPublicWorkbook(
                    workbook,
                    Author.from(author),
                    paths);
        }
    }
}