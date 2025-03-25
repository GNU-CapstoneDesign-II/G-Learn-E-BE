package gnu.capstone.G_Learn_E.global.auth.exception;

import gnu.capstone.G_Learn_E.global.error.exception.client.NotFoundGroupException;

public class AuthNotFoundException extends NotFoundGroupException {
    public AuthNotFoundException(String message) {
        super(message);
    }

    public static AuthNotFoundException emailAuthCodeNotFound() {
        return new AuthNotFoundException("발급된 인증 코드가 존재하지 않습니다.");
    }

    public static AuthNotFoundException expiredEmailAuthCode() {
        return new AuthNotFoundException("인증 코드가 만료되었습니다. 인증 코드를 재발급 해주세요.");
    }
}
