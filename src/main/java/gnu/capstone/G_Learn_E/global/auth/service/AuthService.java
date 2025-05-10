package gnu.capstone.G_Learn_E.global.auth.service;

import gnu.capstone.G_Learn_E.domain.user.entity.User;
import gnu.capstone.G_Learn_E.domain.user.repository.UserRepository;
import gnu.capstone.G_Learn_E.global.auth.exception.AuthInvalidException;
import gnu.capstone.G_Learn_E.global.auth.exception.AuthNotFoundException;
import gnu.capstone.G_Learn_E.global.mail.repository.EmailAuthCodeRepository;
import gnu.capstone.G_Learn_E.global.mail.repository.PasswordResetCodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    @Qualifier("inMemoryEmailAuthCodeRepository")
    private final EmailAuthCodeRepository emailAuthCodeRepository;
    @Qualifier("inMemoryPasswordResetCodeRepository")
    private final PasswordResetCodeRepository passwordResetCodeRepository;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${mail-auth.code.length}")
    private int emailAuthCodeLength;

    private final Random random = new Random();


    /**
     * 로그인
     * @param email 이메일
     * @param password 비밀번호
     * @return 로그인한 유저
     */
    public User login(String email, String password) {
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(AuthNotFoundException::userNotFound);
        if(!passwordEncoder.matches(password, user.getPassword())) {
            log.info("비밀번호가 일치하지 않습니다. [email: {}]", email);
            throw AuthInvalidException.passwordNotMatch();
        }

        return user;
    }


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


    public void updatePassword(User user, String oldPassword, String newPassword) {
        if(!passwordEncoder.matches(oldPassword, user.getPassword())) {
            log.info("비밀번호가 일치하지 않습니다. [userId: {}]", user.getId());
            throw AuthInvalidException.passwordNotMatch();
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public String issuePasswordResetCode(String email) {
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(AuthNotFoundException::userNotFound);
        String resetCode = generateEmailAuthCode();
        passwordResetCodeRepository.saveAuthCode(email, resetCode);
        log.info("비밀번호 재설정 코드 발급 성공 [email: {}, resetCode: {}]", email, resetCode);
        return resetCode;
    }

    public void verifyPasswordResetCode(String email, String resetCode) {
        if(!passwordResetCodeRepository.isIssuedEmail(email)) {
            log.info("발급된 비밀번호 재설정 코드가 없습니다. [email: {}]", email);
            throw new AuthNotFoundException("발급된 비밀번호 재설정 코드가 없습니다.");
        }
        String savedResetCode = passwordResetCodeRepository.findAuthCodeByEmail(email);
        if(!savedResetCode.equals(resetCode)) {
            log.info("비밀번호 재설정 코드 검증 실패 [email: {}, resetCode: {}]", email, resetCode);
            throw new AuthInvalidException("비밀번호 재설정 코드 검증 실패");
        }
        passwordResetCodeRepository.deleteByEmail(email);
        log.info("비밀번호 재설정 코드 검증 성공 [email: {}, resetCode: {}]", email, resetCode);
    }

    public void resetPassword(String name, String email, String newPassword) {
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(AuthNotFoundException::userNotFound);
        if(!user.getName().equals(name)) {
            log.info("이름이 일치하지 않습니다. [name: {}, email: {}]", name, email);
            throw new AuthInvalidException("이름이 일치하지 않습니다.");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
