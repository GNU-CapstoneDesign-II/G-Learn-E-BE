package gnu.capstone.G_Learn_E.global.common.dto.response;

import gnu.capstone.G_Learn_E.domain.workbook.entity.Workbook;

import java.util.List;

public record PublicWorkbook(
        Long id,
        String name,
        Integer coverImage,
        String createdAt,
        Author author,
        boolean downloaded,
        List<PublicPath> paths
) {
    public static PublicWorkbook from(
            Workbook workbook, boolean downloaded, List<PublicPath> paths
    ) {
        return new PublicWorkbook(
                workbook.getId(),
                workbook.getName(),
                workbook.getCoverImage(),
                workbook.getCreatedAt().toString(),
                Author.from(workbook.getAuthor()),
                downloaded,
                paths
        );
    }
}
