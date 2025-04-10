package gnu.capstone.G_Learn_E.global.fastapi.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record ApiLogResponse(
        List<ApiLog> logs
) {
    public record ApiLog(
            Long id,
            LocalDateTime timestamp,
            String apiUrl,
            String method,
            String parameters,
            TokenUsageResponse tokenUsage
    ) {
    }
}
