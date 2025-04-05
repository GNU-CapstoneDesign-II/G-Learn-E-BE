package gnu.capstone.G_Learn_E.domain.user.dto.response;

import gnu.capstone.G_Learn_E.domain.user.entity.User;

import java.util.List;

public record NicknameUpdateResponse(List<NicknameInfo> notifications) {
    public static NicknameUpdateResponse from(User user) {
        return new NicknameUpdateResponse(
                List.of(new NicknameInfo(user.getId(), user.getNickname()))
        );
    }

    public record NicknameInfo(Long id, String nickname) {}
}
