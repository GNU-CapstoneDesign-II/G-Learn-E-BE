package gnu.capstone.G_Learn_E.global.auth.repository.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository("inMemoryEmailAuthCodeRepository")
public class InMemoryEmailAuthCodeRepository implements EmailAuthCodeRepository {

    private final long expirationTime; // 밀리초 단위 (ex. 5분: 5 * 60 * 1000)

    private final Map<String, EmailAuthInfo> emailAuthCodeMap = new ConcurrentHashMap<>();

    public InMemoryEmailAuthCodeRepository(
            @Value("${auth.email-auth-code-expiration-time}") long expirationTime
    ) {
        this.expirationTime = expirationTime;
    }

    @Override
    public void save(String email, String authCode) {
        emailAuthCodeMap.put(email, new EmailAuthInfo(authCode, System.currentTimeMillis()));
    }

    @Override
    public String findByEmail(String email) {
        EmailAuthInfo info = emailAuthCodeMap.get(email);
        if (info == null || isExpired(info.timestamp)) {
            emailAuthCodeMap.remove(email); // 만료되었으면 제거
            return null;
        }
        return info.code;
    }

    @Override
    public boolean exists(String email, String authCode) {
        EmailAuthInfo info = emailAuthCodeMap.get(email);
        if (info == null || isExpired(info.timestamp)) {
            emailAuthCodeMap.remove(email); // 만료되었으면 제거
            return false;
        }
        return info.code.equals(authCode);
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
