package gnu.capstone.G_Learn_E.global.common.dto.response;


import gnu.capstone.G_Learn_E.domain.workbook.entity.Workbook;

import java.time.LocalDateTime;

public record PrivateWorkbook(
        Long id,
        String name,
        Integer coverImage,
        LocalDateTime createdAt,
        Author author,
        boolean uploaded
) {

    public static PrivateWorkbook from(
            Workbook workbook
    ) {
        return new PrivateWorkbook(
                workbook.getId(),
                workbook.getName(),
                workbook.getCoverImage(),
                workbook.getCreatedAt(),
                Author.from(workbook.getAuthor()),
                workbook.isUploaded()
        );
    }
}
