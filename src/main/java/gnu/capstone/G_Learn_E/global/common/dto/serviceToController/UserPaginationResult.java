package gnu.capstone.G_Learn_E.global.common.dto.serviceToController;

import gnu.capstone.G_Learn_E.domain.user.entity.User;
import gnu.capstone.G_Learn_E.global.common.dto.response.PageInfo;

import java.util.List;

public record UserPaginationResult(
        PageInfo pageInfo,
        List<User> userList
) {
    public static UserPaginationResult from(
            PageInfo pageInfo,
            List<User> userList
    ) {
        return new UserPaginationResult(pageInfo, userList);
    }
}
