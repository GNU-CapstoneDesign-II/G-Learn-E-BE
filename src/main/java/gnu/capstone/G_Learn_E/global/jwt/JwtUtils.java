package gnu.capstone.G_Learn_E.global.jwt;

import gnu.capstone.G_Learn_E.domain.user.entity.User;
import gnu.capstone.G_Learn_E.global.jwt.dto.SubjectAndType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;


@Slf4j
@Component
public class JwtUtils {

    private final SecretKey key;
    private final long emailAuthTokenExpiration; // 이메일 인증코드용 토큰
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtUtils(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.expiration-time.email-auth-token}") long emailAuthTokenExpiration,
            @Value("${jwt.expiration-time.access-token}") long accessTokenExpiration,
            @Value("${jwt.expiration-time.refresh-token}") long refreshTokenExpiration
    ) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        this.emailAuthTokenExpiration = emailAuthTokenExpiration;
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public String generateAccessToken(User user) {
        return generateToken(user, accessTokenExpiration, "access");
    }

    public String generateRefreshToken(User user) {
        return generateToken(user, refreshTokenExpiration, "refresh");
    }

    public String generateEmailAuthToken(String email) {
        return generateToken(emailAuthTokenExpiration, "email-auth", email);
    }

    private String generateToken(User user, long expirationTime, String tokenType) {
        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .claim("tokenType", tokenType)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key)
                .compact();
    }
    private String generateToken(long expirationTime, String tokenType, String subject) {
        return Jwts.builder()
                .subject(subject)
                .claim("tokenType", tokenType)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key)
                .compact();
    }


    // 헤더에서 토큰 추출
    public String extractToken(String authorizationHeader) {
        return authorizationHeader.substring(7);
    }
    public String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        return (header != null && header.startsWith("Bearer ")) ? header.substring(7) : null;
    }


    public String getSubject(String token) {
        Claims claims = parseClaims(token);
        return claims.getSubject();
    }

    public String getTokenType(String token) {
        return parseClaims(token).get("tokenType", String.class);
    }

    public SubjectAndType getSubjectAndType(String token) {
        Claims claims = parseClaims(token);
        return new SubjectAndType(claims.getSubject(), claims.get("tokenType", String.class));
    }


    public Map<String, Object> extractClaims(String token) {
        return parseClaims(token);
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            return true;
        } catch (Exception e){
            // 토큰 만료 이외의 JWT 오류는 보안상 유효하지 않은 토큰 예외로 통일
            log.error("Invalid JWT format.");
            return false;
        }
    }

    public boolean isExpired(String token) {
        try {
            Claims claims = parseClaims(token);
            return claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (Exception e){
            log.error("Invalid JWT format.");
            return true;
        }
    }
}