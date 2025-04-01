package gnu.capstone.G_Learn_E.global.auth.dto.response;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {
}
