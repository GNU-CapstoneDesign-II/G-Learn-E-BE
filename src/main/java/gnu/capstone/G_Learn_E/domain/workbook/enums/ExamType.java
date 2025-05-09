package gnu.capstone.G_Learn_E.domain.workbook.enums;

public enum ExamType {

    ALL, // 전체 범위
    MIDDLE, // 중간고사
    FINAL, // 기말고사
    OTHER // 기타
    ;

    public static ExamType fromString(String examType) {
        for (ExamType type : ExamType.values()) {
            if (type.name().equalsIgnoreCase(examType)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid exam type: " + examType);
    }
}
