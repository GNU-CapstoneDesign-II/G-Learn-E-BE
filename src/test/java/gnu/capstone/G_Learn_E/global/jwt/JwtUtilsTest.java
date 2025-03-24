package gnu.capstone.G_Learn_E.global.jwt;

import gnu.capstone.G_Learn_E.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Base64;

import static org.assertj.core.api.Assertions.*;

public class JwtUtilsTest {

    private JwtUtils jwtUtils;
    private final String secretKey = Base64.getEncoder().encodeToString("my-very-secret-key-my-very-secret-key".getBytes());
    private final long accessTokenExpiration = 1000 * 60 * 60; // 1시간
    private final long refreshTokenExpiration = 1000 * 60 * 60 * 24 * 7; // 7일
    private final long emailAuthTokenExpiration = 1000 * 60 * 5; // 5분

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils(secretKey, emailAuthTokenExpiration, accessTokenExpiration, refreshTokenExpiration);
    }

    @Test
    void validateToken_정상토큰이면_true() {
        // given
        User user = User.builder().email("user@example.com").nickname("tester").build();
        String token = jwtUtils.generateAccessToken(user);

        // when
        boolean isValid = jwtUtils.validateToken(token);

        // then
        assertThat(isValid).isTrue();
    }

    @Test
    void validateToken_만료된토큰도_true() throws InterruptedException {
        // given
        User user = User.builder().email("expired@example.com").nickname("expiredUser").build();
        JwtUtils shortLivedJwt = new JwtUtils(secretKey, emailAuthTokenExpiration, 1, refreshTokenExpiration);
        String token = shortLivedJwt.generateAccessToken(user);
        Thread.sleep(5); // 토큰 만료까지 대기

        // when
        boolean isValid = shortLivedJwt.validateToken(token);

        // then
        assertThat(isValid).isTrue(); // ✅ 유효한 형식이므로 true
    }

    @Test
    void validateToken_잘못된토큰이면_false() {
        // given
        String token = "fake.invalid.token";

        // when
        boolean isValid = jwtUtils.validateToken(token);

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    void isExpired_정상토큰이면_false() {
        // given
        User user = User.builder().email("user2@example.com").nickname("tester2").build();
        String token = jwtUtils.generateAccessToken(user);

        // when
        boolean expired = jwtUtils.isExpired(token);

        // then
        assertThat(expired).isFalse();
    }

    @Test
    void isExpired_만료된토큰이면_true() throws InterruptedException {
        // given
        User user = User.builder().email("expired2@example.com").nickname("expiredUser2").build();
        JwtUtils shortLivedJwt = new JwtUtils(secretKey, emailAuthTokenExpiration, 1, refreshTokenExpiration);
        String token = shortLivedJwt.generateAccessToken(user);
        Thread.sleep(5); // 만료 대기

        // when
        boolean expired = shortLivedJwt.isExpired(token);

        // then
        assertThat(expired).isTrue();
    }

    @Test
    void isExpired_형식잘못된토큰이면_true() {
        // given
        String token = "broken.token.payload";

        // when
        boolean expired = jwtUtils.isExpired(token);

        // then
        assertThat(expired).isTrue(); // 예외 발생 → true 반환
    }

    @Test
    void extractToken_헤더에서_정상추출() {
        // given
        String fakeToken = "my.jwt.token";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + fakeToken);

        // when
        String extracted = jwtUtils.extractToken(request);

        // then
        assertThat(extracted).isEqualTo(fakeToken);
    }

    @Test
    void extractToken_헤더없으면_null() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();

        // when
        String extracted = jwtUtils.extractToken(request);

        // then
        assertThat(extracted).isNull();
    }
}
