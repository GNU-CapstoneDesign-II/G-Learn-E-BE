package gnu.capstone.G_Learn_E.global.security.exception.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import gnu.capstone.G_Learn_E.global.security.exception.SecurityAuthException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    // 인증되지 않은 사용자가 접근할 때 401 Unauthorized 에러를 반환
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {

        makeUnauthorizedResponse(SecurityAuthException.noAuthentication(), response);
    }

    private void makeUnauthorizedResponse(Exception e, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> errorDetails = new HashMap<>();

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        errorDetails.put("code", HttpServletResponse.SC_UNAUTHORIZED);
        errorDetails.put("message", e.getMessage());

        response.getWriter().write(mapper.writeValueAsString(errorDetails));
    }
}
