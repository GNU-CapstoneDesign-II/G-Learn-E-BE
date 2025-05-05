package gnu.capstone.G_Learn_E.domain.user.dto.response;

import gnu.capstone.G_Learn_E.domain.user.entity.User;

public record UserInfoResponse(
        Long id,
        String email,
        String nickname,
        Integer profileImage,
        Short level,
        Integer exp
) {
    public static UserInfoResponse from(User user) {
        return new UserInfoResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getProfileImage(),
                user.getLevel(),
                user.getExp()
        );
    }
}