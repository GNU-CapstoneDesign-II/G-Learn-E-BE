package gnu.capstone.G_Learn_E.global.jwt.service;

import gnu.capstone.G_Learn_E.domain.user.entity.User;
import gnu.capstone.G_Learn_E.global.jwt.dto.SubjectAndType;
import gnu.capstone.G_Learn_E.global.jwt.enums.JwtTokenType;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

/**
 * JWT 생성/파싱/유효성(형식) 체크만 담당 (비즈니스 로직은 없음).
 */
@Slf4j
@Component
public class JwtUtils {

    private final SecretKey key;

    @Getter
    private final long emailAuthTokenExpiration; // 이메일 인증코드용 토큰
    @Getter
    private final long accessTokenExpiration;
    @Getter
    private final long refreshTokenExpiration;
    @Getter
    private final long passwordResetTokenExpiration;

    public JwtUtils(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.expiration-time.email-auth-token}") long emailAuthTokenExpiration,
            @Value("${jwt.expiration-time.password-reset-token}") long passwordResetTokenExpiration,
            @Value("${jwt.expiration-time.access-token}") long accessTokenExpiration,
            @Value("${jwt.expiration-time.refresh-token}") long refreshTokenExpiration
    ) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        this.emailAuthTokenExpiration = emailAuthTokenExpiration;
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
        this.passwordResetTokenExpiration = passwordResetTokenExpiration;
    }

    // ----------------- 토큰 생성 -----------------
    public String generateAccessToken(User user) {
        return generateToken(String.valueOf(user.getId()), accessTokenExpiration, JwtTokenType.ACCESS.getType());
    }

    public String generateRefreshToken(User user) {
        return generateToken(String.valueOf(user.getId()), refreshTokenExpiration, JwtTokenType.REFRESH.getType());
    }

    public String generateEmailAuthToken(String email) {
        return generateToken(email, emailAuthTokenExpiration, JwtTokenType.EMAIL_AUTH.getType());
    }

    public String generatePasswordResetToken(String email) {
        return generateToken(email, emailAuthTokenExpiration, JwtTokenType.PASSWORD_RESET.getType());
    }

    private String generateToken(String subject, long expirationTime, String tokenType) {
        return Jwts.builder()
                .subject(subject)
                .claim("tokenType", tokenType)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key)
                .compact();
    }

    // ----------------- 토큰 파싱/검증 -----------------
    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 토큰 포맷이 정상이면 true, (만료 제외) 형식 오류면 false
     * (만료된 토큰은 일단 "형식은 맞으니 true"로 처리)
     */
    public boolean validateFormat(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            // 만료 자체는 "형식은 정상"
            return true;
        } catch (Exception e) {
            log.error("Invalid JWT format. {}", e.getMessage());
            return false;
        }
    }

    /**
     * 만료 여부만 확인
     */
    public boolean isExpired(String token) {
        try {
            return parseClaims(token).getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            // 토큰 구조상 문제
            log.error("JWT parse error: {}", e.getMessage());
            return true;
        }
    }

    // ----------------- 기타 유틸 -----------------
    public String getSubject(String token) {
        return parseClaims(token).getSubject();
    }

    public String getTokenType(String token) {
        return parseClaims(token).get("tokenType", String.class);
    }

    public SubjectAndType getSubjectAndType(String token) {
        Claims claims = parseClaims(token);
        return new SubjectAndType(claims.getSubject(), claims.get("tokenType", String.class));
    }

    public long getRemainingTime(String token) {
        try {
            return parseClaims(token).getExpiration().getTime() - System.currentTimeMillis();
        } catch (Exception e) {
            return 0;
        }
    }

    // ----------------- HTTP Request에서 추출 -----------------
    public String extractToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return null;
        }
        return authorizationHeader.substring(7);
    }

    public String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        return extractToken(header);
    }
}
