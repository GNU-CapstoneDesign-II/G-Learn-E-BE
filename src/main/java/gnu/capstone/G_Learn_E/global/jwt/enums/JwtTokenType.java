package gnu.capstone.G_Learn_E.global.jwt.enums;

public enum JwtTokenType {
    ACCESS("access"),
    REFRESH("refresh"),
    EMAIL_AUTH("email-auth"),
    PASSWORD_RESET("password-reset");

    private final String type;

    JwtTokenType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static JwtTokenType fromString(String type) {
        for (JwtTokenType tokenType : JwtTokenType.values()) {
            if (tokenType.getType().equalsIgnoreCase(type)) {
                return tokenType;
            }
        }
        throw new IllegalArgumentException("Unknown token type: " + type);
    }
}
