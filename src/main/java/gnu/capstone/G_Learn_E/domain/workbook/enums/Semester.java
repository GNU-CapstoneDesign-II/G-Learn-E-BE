package gnu.capstone.G_Learn_E.domain.workbook.enums;

public enum Semester {
    SPRING, // 1학기
    SUMMER, // 여름 계절학기
    FALL, // 2학기
    WINTER, // 겨울 계절학기
    OTHER // 기타
    ;

    public static Semester fromString(String semester) {
        for (Semester sem : Semester.values()) {
            if (sem.name().equalsIgnoreCase(semester)) {
                return sem;
            }
        }
        throw new IllegalArgumentException("Invalid semester: " + semester);
    }
}
