package gnu.capstone.G_Learn_E.global.jwt.repository;

public interface RefreshTokenRepository {
    void save(Long userId, String refreshToken);
    String findByUserId(Long userId);
    void deleteByUserId(Long userId);
}
