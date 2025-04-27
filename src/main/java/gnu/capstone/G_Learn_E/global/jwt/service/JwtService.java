package gnu.capstone.G_Learn_E.global.jwt.service;

import gnu.capstone.G_Learn_E.domain.user.entity.User;
import gnu.capstone.G_Learn_E.global.jwt.dto.SubjectAndType;
import gnu.capstone.G_Learn_E.global.jwt.enums.JwtTokenType;
import gnu.capstone.G_Learn_E.global.jwt.repository.TokenBlacklistRepository;
import gnu.capstone.G_Learn_E.global.jwt.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 외부(Controller 등)에서는 JwtService만 쓰도록 설계.
 * - 토큰 형식 검사
 * - 토큰 추출
 * - 토큰 생성
 * - 리프레시 저장/블랙리스트 처리
 * - 재발급
 * - 로그아웃
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtUtils jwtUtils;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenBlacklistRepository blacklistRepository;

    // ----------------- 토큰 추출 -----------------
    public String extractToken(HttpServletRequest request) {
        return jwtUtils.extractToken(request);
    }

    // ----------------- 토큰 생성 -----------------
    public String generateAccessToken(User user) {
        return jwtUtils.generateAccessToken(user);
    }

    public String generateEmailAuthToken(String email) {
        return jwtUtils.generateEmailAuthToken(email);
    }

    public String generateAndStoreRefreshToken(User user) {
        Long userId = user.getId();
        String newToken = jwtUtils.generateRefreshToken(user);

        String oldToken = refreshTokenRepository.findByUserId(userId);
        if (oldToken != null && !oldToken.equals(newToken)) {
            long remain = jwtUtils.getRemainingTime(oldToken);
            // 블랙리스트 등록 (기존 토큰)
            blacklistRepository.add(oldToken, remain);
        }

        // 최신 리프레시 토큰 DB 저장
        refreshTokenRepository.save(userId, newToken);

        return newToken;
    }

    // -------------------- 토큰 형식/만료 여부 검사 --------------------
    public boolean validateFormat(String token) {
        return jwtUtils.validateFormat(token);
    }
    public boolean isExpired(String token) {
        return jwtUtils.isExpired(token);
    }
    public boolean isBlacklisted(String token) {
        return blacklistRepository.isBlacklisted(token);
    }

    // -------------------- Claims 추출 --------------------
    public SubjectAndType getSubjectAndType(String token) {
        return jwtUtils.getSubjectAndType(token);
    }

    // ----------------- 엑세스 토큰 재발급 -----------------
    public String reissueAccessToken(User user, String refreshToken) {
        // 1번 2번은 JwtAuthenticationFilter에서 처리
//        // 1) 토큰 형식 검사
//        if (!jwtUtils.validateFormat(refreshToken)) {
//            throw new RuntimeException("리프레시 토큰의 형식이 유효하지 않습니다.");
//        }
//
//        // 2) 만료 여부, 타입 확인
//        boolean isExpired = jwtUtils.isExpired(refreshToken);
//        if (isExpired) {
//            throw new RuntimeException("리프레시 토큰이 만료되었습니다.");
//        }

        // JwtAuthenticationFilter + JwtUtils 이중 체크
        String tokenType = jwtUtils.getTokenType(refreshToken);

        // 3) 블랙리스트 확인
        if (blacklistRepository.isBlacklisted(refreshToken)) {
            throw new RuntimeException("블랙리스트에 등록된 리프레시 토큰입니다.");
        }

        // 4) 저장된 토큰과 일치하는지 확인
        String subject = jwtUtils.getSubject(refreshToken);
        Long userId = Long.parseLong(subject);
        String stored = refreshTokenRepository.findByUserId(userId);
        if (!refreshToken.equals(stored)) {
            throw new RuntimeException("저장된 리프레시 토큰과 불일치합니다.");
        }

        // 5) 새 토큰 발급
        return jwtUtils.generateAccessToken(user);
    }

    // ----------------- 로그아웃 -----------------
    public void logout(User user, String accessToken) {
        String tokenType = jwtUtils.getTokenType(accessToken);
        if (!tokenType.equals(JwtTokenType.ACCESS.getType())) {
            throw new RuntimeException("access 타입의 토큰이 아닙니다.");
        }
        long remain = jwtUtils.getRemainingTime(accessToken);
        blacklistRepository.add(accessToken, remain);

        String refreshToken = refreshTokenRepository.findByUserId(user.getId());
        if (refreshToken != null) {
            blacklistRepository.add(refreshToken, remain);
        }

        refreshTokenRepository.deleteByUserId(user.getId());
        log.info("로그아웃 처리 완료 [userId: {}]", user.getId());
    }

    // ----------------- 기타 메서드 -----------------
    public long getEmailAuthTokenExpiration() {
        return jwtUtils.getEmailAuthTokenExpiration();
    }

    public boolean validateRefreshTokenFormat(String token) {
        if (!jwtUtils.validateFormat(token)) return false;
        return !jwtUtils.isExpired(token);
    }

    public void setBlacklistToken(String token) {
        long remain = jwtUtils.getRemainingTime(token);
        blacklistRepository.add(token, remain);
    }
}
