package gnu.capstone.G_Learn_E.global.auth.dto.request;

public record PasswordForgotRequest(
        String name,
        String email,
        String password,
        String passwordConfirm
) {
}
