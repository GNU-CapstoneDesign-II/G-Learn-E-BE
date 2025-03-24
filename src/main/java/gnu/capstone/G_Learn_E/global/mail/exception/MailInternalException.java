package gnu.capstone.G_Learn_E.global.mail.exception;

import gnu.capstone.G_Learn_E.global.error.exception.server.InternalServerErrorGroupException;

public class MailInternalException extends InternalServerErrorGroupException {
    public MailInternalException(String message) {
        super(message);
    }

    public static MailInternalException sendFail(){
        return new MailInternalException("메일 전송에 실패하였습니다.");
    }
}
