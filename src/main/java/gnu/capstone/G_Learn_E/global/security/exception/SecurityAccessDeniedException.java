package gnu.capstone.G_Learn_E.global.security.exception;

import gnu.capstone.G_Learn_E.global.error.exception.client.AccessDeniedGroupException;

public class SecurityAccessDeniedException extends AccessDeniedGroupException {
    public SecurityAccessDeniedException(String message) {
        super(message);
    }

    public static SecurityAccessDeniedException accessDenied() {
        return new SecurityAccessDeniedException("해당 요청에 대한 권한이 없습니다.");
    }

    public static SecurityAccessDeniedException alreadyAuthenticated() {
        return new SecurityAccessDeniedException("이미 인증된 사용자입니다.");
    }
}
