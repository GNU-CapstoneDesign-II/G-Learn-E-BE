package gnu.capstone.G_Learn_E.global.jwt.repository;

public interface TokenBlacklistRepository {
    void add(String refreshToken, long expirationMillis);
    boolean isBlacklisted(String refreshToken);
}


