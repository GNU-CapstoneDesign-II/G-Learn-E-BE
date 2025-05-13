package gnu.capstone.G_Learn_E.domain.public_folder.dto.response;

import gnu.capstone.G_Learn_E.domain.user.entity.User;
import gnu.capstone.G_Learn_E.domain.workbook.entity.Workbook;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public record WorkbookResponse(
        Long id,
        String name,
        Integer coverImage,
        LocalDateTime createdAt,
        Author author,
        boolean downloaded
) {
    public static WorkbookResponse from(Workbook workbook, boolean downloaded) {
        return new WorkbookResponse(
                workbook.getId(),
                workbook.getName(),
                workbook.getCoverImage(),
                workbook.getCreatedAt(),
                Author.from(workbook.getAuthor()),
                downloaded
        );
    }

    public record Author(
            Long userId,
            String name,
            String nickname
    ) {
        public static Author from(User user) {
            return new Author(
                    user.getId(),
                    user.getName(),
                    user.getNickname()
            );
        }
    }
}