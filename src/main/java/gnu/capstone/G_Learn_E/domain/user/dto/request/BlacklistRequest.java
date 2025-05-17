package gnu.capstone.G_Learn_E.domain.user.dto.request;

public record BlacklistRequest(
        Long targetId,
        String blacklistType
) {
}
