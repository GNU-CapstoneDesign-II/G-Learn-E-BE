package gnu.capstone.G_Learn_E.global.jwt.repository;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ConcurrentHashMap;

@Repository("inMemoryTokenBlacklistRepository")
public class InMemoryTokenBlacklistRepository implements TokenBlacklistRepository {

    private final ConcurrentHashMap<String, Long> blacklist = new ConcurrentHashMap<>();

    @Override
    public void add(String token, long expirationMillis) {
        long expirationTime = System.currentTimeMillis() + expirationMillis;
        blacklist.put(token, expirationTime);
    }

    @Override
    public boolean isBlacklisted(String token) {
        Long expireAt = blacklist.get(token);
        if (expireAt == null) return false;
        if (System.currentTimeMillis() > expireAt) {
            blacklist.remove(token); // 만료된 항목은 제거
            return false;
        }
        return true;
    }

    // 10분마다 만료된 항목 제거
    @Scheduled(fixedRate = 10 * 60 * 1000)
    public void cleanExpiredTokens() {
        long now = System.currentTimeMillis();
        blacklist.entrySet().removeIf(entry -> entry.getValue() < now);
    }
}
