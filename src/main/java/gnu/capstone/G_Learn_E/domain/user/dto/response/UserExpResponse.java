package gnu.capstone.G_Learn_E.domain.user.dto.response;

import gnu.capstone.G_Learn_E.domain.user.entity.User;

import java.util.List;

public record UserExpResponse(List<ExpInfo> exp) {

    public record ExpInfo(Long id, Short level, Integer experience) {
        public static ExpInfo from(User user) {
            return new ExpInfo(user.getId(), user.getLevel(), user.getExp());
        }
    }

    public static UserExpResponse from(User user) {
        return new UserExpResponse(List.of(ExpInfo.from(user)));
    }

    public static UserExpResponse from(List<User> users) {
        return new UserExpResponse(users.stream().map(ExpInfo::from).toList());
    }
}
