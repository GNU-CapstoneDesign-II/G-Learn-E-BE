package gnu.capstone.G_Learn_E.global.auth.service;

import gnu.capstone.G_Learn_E.domain.user.repository.UserRepository;
import gnu.capstone.G_Learn_E.global.auth.exception.AuthInvalidException;
import gnu.capstone.G_Learn_E.global.auth.repository.email.EmailAuthCodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    @Qualifier("inMemoryEmailAuthCodeRepository")
    private final EmailAuthCodeRepository emailAuthCodeRepository;

    private final UserRepository userRepository;

    @Value("${mail-auth.code.length}")
    private int emailAuthCodeLength;

    private final Random random = new Random();

    /**
     * 이메일 인증 코드 발급
     * @param email 이메일
     * @return 인증 코드 (6자리 숫자)
     */
    public String issueEmailAuthCode(String email) {
        if(userRepository.existsByEmail(email)) {
            // 이미 가입된 이메일인 경우
            log.info("이미 가입된 이메일입니다. [email: {}]", email);
            throw AuthInvalidException.existsUser();
        }
        if(emailAuthCodeRepository.findByEmail(email) != null) {
            // 이미 인증 코드가 발급된 이메일인 경우
            log.info("이미 인증 코드가 발급된 이메일입니다. [email: {}]", email);
            throw AuthInvalidException.existsEmailAuthCode();
        }
        String authCode = generateEmailAuthCode();
        emailAuthCodeRepository.save(email, authCode);
        log.info("이메일 인증 코드 발급 성공 [email: {}, authCode: {}]", email, authCode);
        return authCode;
    }


    private String generateEmailAuthCode() {
        // 6자리 숫자로 구성된 인증 코드 생성
        return String.valueOf(random.nextInt((int) Math.pow(10, emailAuthCodeLength)));
    }
}
