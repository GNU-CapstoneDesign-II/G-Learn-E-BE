package gnu.capstone.G_Learn_E.global.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import gnu.capstone.G_Learn_E.domain.user.entity.User;
import gnu.capstone.G_Learn_E.domain.user.repository.UserRepository;
import gnu.capstone.G_Learn_E.global.error.exception.client.NotFoundGroupException;
import gnu.capstone.G_Learn_E.global.jwt.dto.SubjectAndType;
import gnu.capstone.G_Learn_E.global.jwt.enums.JwtTokenType;
import gnu.capstone.G_Learn_E.global.jwt.exception.JwtAuthException;
import gnu.capstone.G_Learn_E.global.jwt.service.JwtService;
import gnu.capstone.G_Learn_E.global.security.SecurityPathProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final SecurityPathProperties securityPathProperties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher(); // Ant 패턴 매칭 객체

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // 1. 토큰 추출
            String token = jwtService.extractToken(request);

            String requestURI = request.getRequestURI();

            boolean isEmailAuthPath = securityPathProperties.emailAuth().stream()
                    .anyMatch(pattern -> pathMatcher.match(pattern, requestURI)); // AntPathMatcher 사용
            boolean isRefreshPath = securityPathProperties.refresh().stream()
                    .anyMatch(pattern -> pathMatcher.match(pattern, requestURI)); // AntPathMatcher 사용
            boolean isPasswordResetPath = securityPathProperties.passwordReset().stream()
                    .anyMatch(pattern -> pathMatcher.match(pattern, requestURI)); // AntPathMatcher 사용

            // 2. 토큰 존재 및 검증
            if (token != null) {
                if(!jwtService.validateFormat(token)) throw JwtAuthException.invalidToken();
                if(jwtService.isExpired(token)) throw JwtAuthException.expired();
                if(jwtService.isBlacklisted(token)) throw JwtAuthException.expired();

                // 3. 토큰에서 subject, tokenType 추출
                SubjectAndType subjectAndType = jwtService.getSubjectAndType(token);
                String subject = subjectAndType.subject(); // subject : PK(access or refresh) or email(email-auth)
                JwtTokenType tokenType = JwtTokenType.fromString(subjectAndType.type()); // tokenType : access, refresh, email-auth

                if(tokenType.equals(JwtTokenType.EMAIL_AUTH)) {
                    // 회원가입용 토큰인 경우
                    if (!isEmailAuthPath) {
                        throw JwtAuthException.authTokenNotAllowed();
                    }
                    request.setAttribute("email", subject);
                } else if(tokenType.equals(JwtTokenType.PASSWORD_RESET)) {
                    // 비밀번호 재설정용 토큰인 경우
                    if (!isPasswordResetPath) {
                        throw JwtAuthException.invalidToken();
                    }
                    request.setAttribute("email", subject);
                } else {
                    if(tokenType.equals(JwtTokenType.REFRESH)){
                        // Access Token인 경우
                        if(!isRefreshPath) {
                            // TODO : 예외 메세지 변경
                            log.error("리프레시 토큰을 사용할 수 없는 경로입니다.");
                            throw JwtAuthException.invalidToken();
                        }
                    }
                    else if(!tokenType.equals(JwtTokenType.ACCESS)){
                        throw JwtAuthException.invalidToken();
                    }

                    // 4. DB에서 유저 정보 조회
                    User user = userRepository.findById(Long.parseLong(subject))
                            .orElseThrow(JwtAuthException::userNotFound);

                    // 5. SecurityContextHolder에 인증 정보 저장
                    var authentication = new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } else {
                // 토큰이 없는 경우
                if (isEmailAuthPath) {
                    throw JwtAuthException.emailAuthTokenRequired();
                }
                // 나머지 토큰이 없는 경우는 Security Filter에서 처리됨
            }
            // 6. 필터 체인 진행
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            makeErrorResponse(e, response);
        }
    }

    private void makeErrorResponse(Exception e, HttpServletResponse response) throws IOException {
        log.error(e.getMessage());
        response.setContentType("application/json;charset=UTF-8");
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> errorDetails = new HashMap<>();

        if(e instanceof JwtAuthException){
            // 토큰 관련 예외 처리
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            errorDetails.put("code", HttpServletResponse.SC_UNAUTHORIZED);
            errorDetails.put("message", e.getMessage());
        }
        else if(e instanceof NotFoundGroupException){
            // 유저 관련 예외 처리
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);

            errorDetails.put("code", HttpServletResponse.SC_NOT_FOUND);
            errorDetails.put("message", e.getMessage());
        }
        else {
            // 기타 예외 처리
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            errorDetails.put("code", HttpServletResponse.SC_BAD_REQUEST);
            errorDetails.put("message", e.getMessage());
        }

        response.getWriter().write(mapper.writeValueAsString(errorDetails));
    }
}