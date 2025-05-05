package gnu.capstone.G_Learn_E.global.mail.repository;

public interface EmailAuthCodeRepository {
    void saveAuthCode(String email, String authCode);

    boolean isIssuedEmail(String email);

    String findAuthCodeByEmail(String email);

    void deleteByEmail(String email);

}
