package gnu.capstone.G_Learn_E.global.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import gnu.capstone.G_Learn_E.domain.user.entity.User;
import gnu.capstone.G_Learn_E.domain.user.service.UserService;
import gnu.capstone.G_Learn_E.global.error.exception.NotFoundGroupException;
import gnu.capstone.G_Learn_E.global.jwt.dto.SubjectAndType;
import gnu.capstone.G_Learn_E.global.jwt.exception.JwtAuthException;
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

    private final JwtUtils jwtUtils;
    private final UserService userService;
    private final SecurityPathProperties securityPathProperties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher(); // Ant 패턴 매칭 객체

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // 1. 토큰 추출
            String token = jwtUtils.extractToken(request);

            // 2. 토큰 존재 및 검증
            if (token != null) {
                if(!jwtUtils.validateToken(token)) throw JwtAuthException.invalidToken();
                if(jwtUtils.isExpired(token)) throw JwtAuthException.expired();

                // 3. 토큰에서 subject, tokenType 추출
                SubjectAndType subjectAndType = jwtUtils.getSubjectAndType(token);
                String subject = subjectAndType.subject(); // subject : PK(access or refresh) or email(email-auth)
                String tokenType = subjectAndType.type(); // tokenType : access, refresh, email-auth

                String requestURI = request.getRequestURI();

                boolean isEmailAuthPath = securityPathProperties.getEmailAuth().stream()
                        .anyMatch(pattern -> pathMatcher.match(pattern, requestURI)); // AntPathMatcher 사용

                if(tokenType.equals("email-auth")){
                    // 회원가입용 토큰인 경우
                    if (!isEmailAuthPath) {
                        throw JwtAuthException.authTokenNotAllowed();
                    }
                    request.setAttribute("email", subject);
                } else if (tokenType.equals("access") || tokenType.equals("refresh")) {
                    // 인증용 토큰인 경우
                    if (isEmailAuthPath) {
                        throw JwtAuthException.authTokenNotAllowed();
                    }

                    // 4. DB에서 유저 정보 조회
                    User user = userService.findById(subject);

                    // 5. SecurityContextHolder에 인증 정보 저장
                    var authentication = new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
                else {
                    throw JwtAuthException.invalidToken();
                }
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