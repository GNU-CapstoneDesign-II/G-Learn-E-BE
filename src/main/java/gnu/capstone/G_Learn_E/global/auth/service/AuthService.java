package gnu.capstone.G_Learn_E.global.auth.service;

import gnu.capstone.G_Learn_E.domain.user.repository.UserRepository;
import gnu.capstone.G_Learn_E.global.auth.exception.AuthInvalidException;
import gnu.capstone.G_Learn_E.global.auth.exception.AuthNotFoundException;
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
        String authCode = generateEmailAuthCode();
        emailAuthCodeRepository.saveAuthCode(email, authCode);
        log.info("이메일 인증 코드 발급 성공 [email: {}, authCode: {}]", email, authCode);
        return authCode;
    }

    public void verifyEmailAuthCode(String email, String authCode) {
        if(!emailAuthCodeRepository.isIssuedEmail(email)) {
            log.info("발급된 인증코드가 없습니다. [email: {}]", email);
            throw AuthNotFoundException.emailAuthCodeNotFound();
        }
        String savedAuthCode = emailAuthCodeRepository.findAuthCodeByEmail(email);
        if(!savedAuthCode.equals(authCode)) {
            log.info("이메일 인증 코드 검증 실패 [email: {}, authCode: {}]", email, authCode);
            throw AuthInvalidException.invalidEmailAuthCode();
        }
        emailAuthCodeRepository.deleteByEmail(email);
        log.info("이메일 인증 코드 검증 성공 [email: {}, authCode: {}]", email, authCode);
    }


    private String generateEmailAuthCode() {
        // 6자리 숫자로 구성된 인증 코드 생성
        return String.valueOf(random.nextInt((int) Math.pow(10, emailAuthCodeLength)));
    }
}
