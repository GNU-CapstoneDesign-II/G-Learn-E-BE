package gnu.capstone.G_Learn_E.domain.public_folder.enums;

import lombok.Getter;

public enum SubjectGrade {
    NO_GRADE_DISTINCT("학년구분없음", 0),
    FIRST_YEAR("1학년", 1),
    SECOND_YEAR("2학년", 2),
    THIRD_YEAR("3학년", 3),
    FOURTH_YEAR("4학년", 4),
    FIFTH_YEAR("5학년", 5),
    ;

    @Getter
    private final String grade;
    @Getter
    private final int order;

    SubjectGrade(String grade, int order) {
        this.grade = grade;
        this.order = order;
    }

}
