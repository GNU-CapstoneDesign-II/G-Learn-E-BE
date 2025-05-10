package gnu.capstone.G_Learn_E.global.jwt.repository;

import org.springframework.stereotype.Repository;

import java.util.concurrent.ConcurrentHashMap;

@Repository("inMemoryRefreshTokenRepository")
public class InMemoryRefreshTokenRepository implements RefreshTokenRepository{

    private final ConcurrentHashMap<Long, String> store = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> storeByEmail = new ConcurrentHashMap<>();

    @Override
    public void save(Long userId, String refreshToken) {
        store.put(userId, refreshToken);
    }

    @Override
    public void save(String email, String refreshToken) {
        storeByEmail.put(email, refreshToken);
    }

    @Override
    public String findByUserId(Long userId) {
        return store.get(userId);
    }

    @Override
    public void deleteByUserId(Long userId) {
        store.remove(userId);
    }

    @Override
    public String findByEmail(String email) {
        return storeByEmail.get(email);
    }

    @Override
    public void deleteByEmail(String email) {
        storeByEmail.remove(email);
    }
}
