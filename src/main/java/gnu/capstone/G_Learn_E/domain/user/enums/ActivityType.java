package gnu.capstone.G_Learn_E.domain.user.enums;

public enum ActivityType {
    LOGIN("로그인"),
    LOGOUT("로그아웃"),
    SIGNUP("회원가입"),
    PASSWORD_RESET("비밀번호 재설정"),
    EMAIL_VERIFICATION("이메일 인증"),
    PROFILE_UPDATE("프로필 수정"),
    COLLEGE_UPDATE("대학 변경"),
    DEPARTMENT_UPDATE("학과 변경"),
    WORKBOOK_CREATE("문제집 생성"),
    WORKBOOK_UPDATE("문제집 수정"),
    SOLVED_WORKBOOK("문제집 풀이"),
    WORKBOOK_UPLOAD("문제집 업로드");

    private final String description;

    ActivityType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
