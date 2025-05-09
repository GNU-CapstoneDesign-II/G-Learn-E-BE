package gnu.capstone.G_Learn_E.domain.user.dto.request;

public record UserInfoUpdateRequest(
        String name,
        String nickname,
        Long collegeId,
        Long departmentId
) {
}
