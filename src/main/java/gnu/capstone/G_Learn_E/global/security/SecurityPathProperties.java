package gnu.capstone.G_Learn_E.global.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "security.path")
public class SecurityPathProperties {
    private List<String> permitAll;
    private List<String> authenticated;
    private List<String> anonymous;
    private List<String> emailAuth;
    private String anyRequest;
}