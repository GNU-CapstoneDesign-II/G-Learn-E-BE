package gnu.capstone.G_Learn_E.global.security.exception.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import gnu.capstone.G_Learn_E.global.security.exception.SecurityAccessDeniedException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        makeAccessDeniedResponse(SecurityAccessDeniedException.accessDenied(), response);
    }

    private void makeAccessDeniedResponse(Exception e, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> errorDetails = new HashMap<>();

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        errorDetails.put("code", HttpServletResponse.SC_FORBIDDEN);
        errorDetails.put("message", e.getMessage());

        response.getWriter().write(mapper.writeValueAsString(errorDetails));
    }
}
