package gnu.capstone.G_Learn_E.global.security.exception;

import gnu.capstone.G_Learn_E.global.error.exception.client.AuthGroupException;

public class SecurityAuthException extends AuthGroupException {
    public SecurityAuthException(String message) {
        super(message);
    }

    public static SecurityAuthException noAuthentication() {
        return new SecurityAuthException("인증되지 않은 사용자입니다. 로그인 후 다시 시도하세요.");
    }
}
