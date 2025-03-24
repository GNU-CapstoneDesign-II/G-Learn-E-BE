package gnu.capstone.G_Learn_E.global.auth.dto.request;

public record EmailAuthCodeVerify(
    String email,
    String authCode
) {
}
