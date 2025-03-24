package gnu.capstone.G_Learn_E.domain.user.exception;

import gnu.capstone.G_Learn_E.global.error.exception.NotFoundGroupException;

public class UserNotFoundException extends NotFoundGroupException {
    public UserNotFoundException(String message) {
        super(message);
    }

    public static UserNotFoundException userNotFound(){
        return new UserNotFoundException("해당 사용자를 찾을 수 없습니다.");
    }
}
