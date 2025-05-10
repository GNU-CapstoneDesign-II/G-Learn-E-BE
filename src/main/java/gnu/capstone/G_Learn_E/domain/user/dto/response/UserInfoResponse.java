package gnu.capstone.G_Learn_E.domain.user.dto.response;

import gnu.capstone.G_Learn_E.domain.public_folder.dto.response.CollegeResponse;
import gnu.capstone.G_Learn_E.domain.public_folder.dto.response.DepartmentResponse;
import gnu.capstone.G_Learn_E.domain.user.entity.User;

public record UserInfoResponse(
        Long id,
        String name,
        String email,
        String nickname,
        Integer profileImage,
        Short level,
        Integer exp,
        Integer expLimit,
        CollegeResponse college,
        DepartmentResponse department
) {
    public static UserInfoResponse from(User user) {
        return new UserInfoResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getNickname(),
                user.getProfileImage(),
                user.getLevel(),
                user.getExp(),
                user.getExpLimit(),
                CollegeResponse.from(user.getCollege()),
                DepartmentResponse.from(user.getDepartment())
        );
    }
}