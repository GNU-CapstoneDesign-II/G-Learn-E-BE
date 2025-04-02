package gnu.capstone.G_Learn_E.domain.public_folder.dto.response;

import gnu.capstone.G_Learn_E.domain.workbook.entity.Workbook;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public record WorkbookResponse(
        Long id,
        String name,
        Integer coverImage,
        LocalDateTime createdAt
) {
    public static WorkbookResponse from(Workbook workbook) {
        return new WorkbookResponse(
                workbook.getId(),
                workbook.getName(),
                workbook.getCoverImage(),
                workbook.getCreatedAt()
        );
    }
}