package gnu.capstone.G_Learn_E.global.auth.dto.request;

public record PasswordChangeRequest(
        String oldPassword,
        String newPassword,
        String newPasswordConfirm
) {
}
