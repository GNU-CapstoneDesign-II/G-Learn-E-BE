package gnu.capstone.G_Learn_E.global.auth.dto.response;

public record AccessTokenResponse(
        String accessToken
) {
    public static AccessTokenResponse of(String accessToken) {
        return new AccessTokenResponse(accessToken);
    }
}
