package gnu.capstone.G_Learn_E.global.jwt.repository;

public interface TokenRepository {
    void save(Long userId, String refreshToken);
    void save(String email, String refreshToken);
    String findByUserId(Long userId);
    void deleteByUserId(Long userId);

    String findByEmail(String email);
    void deleteByEmail(String email);
}
