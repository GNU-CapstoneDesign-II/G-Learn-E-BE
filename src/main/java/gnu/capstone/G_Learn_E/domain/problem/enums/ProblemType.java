package gnu.capstone.G_Learn_E.domain.problem.enums;

public enum ProblemType {
    MULTIPLE, BLANK, OX, DESCRIPTIVE;

    public static ProblemType from(String type) {
        return switch (type) {
            case "MULTIPLE" -> MULTIPLE;
            case "BLANK" -> BLANK;
            case "OX" -> OX;
            case "DESCRIPTIVE" -> DESCRIPTIVE;
            default -> throw new IllegalArgumentException("Invalid problem type: " + type);
        };
    }
}
