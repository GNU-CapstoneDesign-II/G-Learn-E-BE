package gnu.capstone.G_Learn_E.global.auth.exception;

import gnu.capstone.G_Learn_E.global.error.exception.client.InvalidGroupException;

public class AuthInvalidException extends InvalidGroupException {
    public AuthInvalidException(String message) {
        super(message);
    }

    public static AuthInvalidException existsUser() {
        return new AuthInvalidException("이미 존재하는 유저입니다.");
    }

    public static AuthInvalidException existsEmailAuthCode() {
        return new AuthInvalidException("이미 인증 코드가 발급된 이메일입니다.");
    }

    public static AuthInvalidException invalidEmailAuthCode() {
        return new AuthInvalidException("유효하지 않은 인증 코드 입니다. 인증 코드를 재발급 해주세요.");
    }

    public static AuthInvalidException invalidEmailDomain() {
        return new AuthInvalidException("유효하지 않은 이메일 도메인 입니다.");
    }

    public static AuthInvalidException invalidEmailFormat() {
        return new AuthInvalidException("유효하지 않은 이메일 형식 입니다.");
    }
}
