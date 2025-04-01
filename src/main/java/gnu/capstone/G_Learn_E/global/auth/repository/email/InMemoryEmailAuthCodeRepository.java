package gnu.capstone.G_Learn_E.global.auth.repository.email;

import gnu.capstone.G_Learn_E.global.auth.exception.AuthInvalidException;
import gnu.capstone.G_Learn_E.global.auth.exception.AuthNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Repository("inMemoryEmailAuthCodeRepository")
public class InMemoryEmailAuthCodeRepository implements EmailAuthCodeRepository {

    private final long expirationTime; // 밀리초 단위 (ex. 5분: 5 * 60 * 1000)

    private final Map<String, EmailAuthInfo> emailAuthCodeMap = new ConcurrentHashMap<>();

    public InMemoryEmailAuthCodeRepository(
            @Value("${mail-auth.code.expiration-time}") long expirationTime
    ) {
        this.expirationTime = expirationTime * 60 * 1000; // 분을 밀리초로 변환
    }

    @Override
    public void saveAuthCode(String email, String authCode) {
        if(isIssuedEmail(email)) {
            // 이미 인증 코드가 발급된 이메일인 경우
            log.info("이미 인증 코드가 발급된 이메일입니다. [email: {}]. 기존 인증 코드를 삭제합니다.", email);
            deleteByEmail(email);
        }
        emailAuthCodeMap.put(email, new EmailAuthInfo(authCode, System.currentTimeMillis()));
    }

    @Override
    public boolean isIssuedEmail(String email) {
        // 존재 여부만 확인
        EmailAuthInfo info = emailAuthCodeMap.get(email);
        return info != null;
    }

    @Override
    public String findAuthCodeByEmail(String email) {
        EmailAuthInfo info = emailAuthCodeMap.get(email);
        if(info == null) {
            log.info("인증 코드가 존재하지 않습니다. [email: {}]", email);
            throw AuthNotFoundException.emailAuthCodeNotFound();
        }
        if(isExpired(info.timestamp)) {
            log.info("인증 코드가 만료되었습니다. [email: {}]", email);
            deleteByEmail(email);
            throw AuthNotFoundException.expiredEmailAuthCode();
        }
        return info.code;
    }


    @Override
    public void deleteByEmail(String email) {
        emailAuthCodeMap.remove(email);
    }



    private boolean isExpired(long timestamp) {
        return System.currentTimeMillis() - timestamp >= expirationTime;
    }

    // 주기적으로 만료된 인증 코드 정리 (예: 1분마다)
    @Scheduled(fixedDelay = 60 * 1000) // 1분 간격
    public void cleanUpExpiredCodes() {
        emailAuthCodeMap.entrySet().removeIf(entry ->
                isExpired(entry.getValue().timestamp)
        );
    }

    // 내부 저장용 클래스
    private record EmailAuthInfo(String code, long timestamp) { }
}
