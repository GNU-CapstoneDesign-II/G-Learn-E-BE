package gnu.capstone.G_Learn_E.domain.user.dto.response;

import gnu.capstone.G_Learn_E.domain.user.entity.User;

import java.util.List;

public record BlacklistResponse(
        List<BlacklistUser> blacklistUsers
) {

    public static BlacklistResponse from(List<User> users) {
        return new BlacklistResponse(
                users.stream()
                        .map(BlacklistUser::from)
                        .toList()
        );
    }


    private record BlacklistUser(
            Long id,
            String email,
            String nickname,
            Integer profileImage
    ) {
        public static BlacklistUser from(User user) {
            return new BlacklistUser(
                    user.getId(),
                    user.getEmail(),
                    user.getNickname(),
                    user.getProfileImage()
            );
        }
    }
}
