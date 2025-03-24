package gnu.capstone.G_Learn_E.global.auth.repository.email;

public interface EmailAuthCodeRepository {
    void save(String email, String authCode);
    String findByEmail(String email);

    boolean exists(String email, String authCode);

    void deleteByEmail(String email);

}
