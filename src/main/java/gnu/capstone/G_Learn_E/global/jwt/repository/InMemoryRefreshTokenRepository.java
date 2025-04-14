package gnu.capstone.G_Learn_E.global.jwt.repository;

import org.springframework.stereotype.Repository;

import java.util.concurrent.ConcurrentHashMap;

@Repository("inMemoryRefreshTokenRepository")
public class InMemoryRefreshTokenRepository implements RefreshTokenRepository{

    private final ConcurrentHashMap<Long, String> store = new ConcurrentHashMap<>();

    @Override
    public void save(Long userId, String refreshToken) {
        store.put(userId, refreshToken);
    }

    @Override
    public String findByUserId(Long userId) {
        return store.get(userId);
    }

    @Override
    public void deleteByUserId(Long userId) {
        store.remove(userId);
    }
}
