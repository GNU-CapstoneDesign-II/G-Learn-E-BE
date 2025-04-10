package gnu.capstone.G_Learn_E.global.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "security.path")
public record SecurityPathProperties(
        List<String> permitAll,
        List<String> authenticated,
        List<String> anonymous,
        List<String> emailAuth,
        String anyRequest
) {
}
