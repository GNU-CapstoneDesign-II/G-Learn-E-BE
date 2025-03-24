package gnu.capstone.G_Learn_E.global.mail;

import gnu.capstone.G_Learn_E.global.mail.exception.MailInternalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EmailSender {

    private final JavaMailSender mailSender;
    private final String expirationTimeMessage;

    public EmailSender(
            JavaMailSender mailSender,
            @Value("${mail-auth.code.expiration-time}") long expirationTime // 분 단위
    ) {
        this.mailSender = mailSender;
        this.expirationTimeMessage = String.valueOf(expirationTime);
    }

    public void sendAuthCode(String toEmail, String authCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("[G-Learn-E] 이메일 인증 코드");
        message.setText(buildEmailBody(authCode));

        try {
            mailSender.send(message);
            log.info("인증 코드 메일 전송 완료: {}", toEmail);
        } catch (Exception e) {
            log.error("인증 코드 메일 전송 실패: {}", toEmail, e);
            throw MailInternalException.sendFail();
        }
    }

    private String buildEmailBody(String code) {
        return String.format(
                "G-Learn-E 인증 코드입니다.\n\n" +
                        "아래 인증 코드를 입력하여 이메일 인증을 완료해주세요.\n\n" +
                        "인증 코드: %s\n\n" +
                        "이 코드는 %s분간 유효합니다.", code, expirationTimeMessage
        );
    }
}