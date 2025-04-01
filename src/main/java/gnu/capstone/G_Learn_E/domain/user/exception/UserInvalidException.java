package gnu.capstone.G_Learn_E.domain.user.exception;

import gnu.capstone.G_Learn_E.global.error.exception.client.InvalidGroupException;

public class UserInvalidException extends InvalidGroupException {
    public UserInvalidException(String message) {
        super(message);
    }

    public static UserInvalidException requestInvalid() {
        return new UserInvalidException("요청이 올바르지 않습니다.");
    }

    public static UserInvalidException existsEmail() {
        return new UserInvalidException("이미 존재하는 이메일입니다.");
    }

    public static UserInvalidException existsNickname() {
        return new UserInvalidException("이미 존재하는 닉네임입니다.");
    }
}
