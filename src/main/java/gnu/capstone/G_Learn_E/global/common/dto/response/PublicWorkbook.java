package gnu.capstone.G_Learn_E.global.common.dto.response;

import gnu.capstone.G_Learn_E.domain.workbook.entity.Workbook;

public record PublicWorkbook(
        Long id,
        String name,
        Integer coverImage,
        String createdAt,
        boolean downloaded
) {
    public static PublicWorkbook from(
            Workbook workbook, boolean downloaded
    ) {
        return new PublicWorkbook(
                workbook.getId(),
                workbook.getName(),
                workbook.getCoverImage(),
                workbook.getCreatedAt().toString(),
                downloaded
        );
    }
}
