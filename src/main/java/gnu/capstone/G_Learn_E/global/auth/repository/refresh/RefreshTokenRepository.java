package gnu.capstone.G_Learn_E.global.auth.repository.refresh;

public interface RefreshTokenRepository {
    void save(Long userId, String refreshToken);
    String findByUserId(Long userId);
    void deleteByUserId(Long userId);
}
