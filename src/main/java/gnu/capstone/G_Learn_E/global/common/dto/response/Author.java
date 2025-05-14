package gnu.capstone.G_Learn_E.global.common.dto.response;

import gnu.capstone.G_Learn_E.domain.user.entity.User;

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
