package gnu.capstone.G_Learn_E.global.jwt.exception;

import gnu.capstone.G_Learn_E.global.error.exception.client.AuthGroupException;

public class JwtAuthException extends AuthGroupException {
    public JwtAuthException(String message) {
        super(message);
    }

    public static JwtAuthException userNotFound(){
        return new JwtAuthException("유저를 찾을 수 없습니다.");
    }

    public static JwtAuthException expired(){
        return new JwtAuthException("JWT 토큰이 만료되었습니다.");
    }

    public static JwtAuthException invalidToken(){
        return new JwtAuthException("유효하지 않은 JWT 토큰입니다.");
    }

    public static JwtAuthException emailAuthTokenRequired(){
        return new JwtAuthException("이메일 인증 완료 토큰이 필요합니다.");
    }

    public static JwtAuthException signupTokenNotAllowed(){
        return new JwtAuthException("이메일 인증 토큰은 회원가입 시에만 사용할 수 있습니다.");
    }

    public static JwtAuthException authTokenNotAllowed(){
        return new JwtAuthException("인증 토큰은 회원가입 시에 사용할 수 없습니다.");
    }
}
