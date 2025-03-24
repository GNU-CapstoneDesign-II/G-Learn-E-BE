package gnu.capstone.G_Learn_E.global.auth.util;

import gnu.capstone.G_Learn_E.global.auth.exception.AuthInvalidException;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "mail-auth")
public class EmailValidator {
    @Setter
    private List<String> allowedDomain;

    public void validate(String email) {
        if (!isValidFormat(email)) {
            throw AuthInvalidException.invalidEmailFormat();
        }

        if (!allowedDomain.contains(extractDomain(email))) {
            throw AuthInvalidException.invalidEmailDomain();
        }
    }

    private boolean isValidFormat(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    private String extractDomain(String email) {
        int atIdx = email.lastIndexOf("@");
        return (atIdx != -1 && atIdx < email.length() - 1)
                ? email.substring(atIdx + 1)
                : "";
    }
}