package gnu.capstone.G_Learn_E.global.auth.exception;

import gnu.capstone.G_Learn_E.global.error.exception.client.InvalidGroupException;

public class AuthInvalidException extends InvalidGroupException {
    public AuthInvalidException(String message) {
        super(message);
    }

    // 로그인
    public static AuthInvalidException existsUser() {
        return new AuthInvalidException("이미 존재하는 유저입니다.");
    }

    public static AuthInvalidException passwordNotMatch() {
        return new AuthInvalidException("비밀번호가 일치하지 않습니다.");
    }



    // 이메일 인증
    public static AuthInvalidException existsEmailAuthCode() {
        return new AuthInvalidException("이미 인증 코드가 발급된 이메일입니다.");
    }

    public static AuthInvalidException invalidEmailAuthCode() {
        return new AuthInvalidException("유효하지 않은 인증 코드 입니다.");
    }

    public static AuthInvalidException invalidEmailDomain() {
        return new AuthInvalidException("유효하지 않은 이메일 도메인 입니다.");
    }

    public static AuthInvalidException invalidEmailFormat() {
        return new AuthInvalidException("유효하지 않은 이메일 형식 입니다.");
    }

    public static AuthInvalidException emailAndTokenNotMatch() {
        return new AuthInvalidException("이메일 인증 토큰과 요청 이메일이 일치하지 않습니다.");
    }
}
