package gnu.capstone.G_Learn_E.global.fastapi.entity;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;


@ConfigurationProperties(prefix = "fast-api")
public record FastApiProperties(
        String baseUrl,
        Map<String, Endpoint> endpoints
) {
    public record Endpoint(String method, String path) {
    }
}
