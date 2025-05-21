package gnu.capstone.G_Learn_E.domain.workbook.enums;

import lombok.Getter;

@Getter
public enum ExamType {

    ALL("전체"), // 전체 범위
    MIDDLE("중간고사"), // 중간고사
    FINAL("기말고사"), // 기말고사
    OTHER("기타") // 기타
    ;

    private final String label;
    ExamType(String label) { this.label = label; }

    public static ExamType fromString(String examType) {
        for (ExamType type : ExamType.values()) {
            if (type.name().equalsIgnoreCase(examType)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid exam type: " + examType);
    }
}
