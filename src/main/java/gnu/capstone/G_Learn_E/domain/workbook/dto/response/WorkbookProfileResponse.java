package gnu.capstone.G_Learn_E.domain.workbook.dto.response;

import gnu.capstone.G_Learn_E.domain.user.entity.User;
import gnu.capstone.G_Learn_E.domain.workbook.entity.Workbook;
import gnu.capstone.G_Learn_E.global.common.dto.response.Author;

public record WorkbookProfileResponse(
        Long id, // 문제집 ID
        String name, // 워크북 이름
        String professor, // 교수 이름
        String examType, // 시험 유형
        Integer coverImage, // 표지 이미지
        Integer courseYear, // 수강 연도
        String semester, // 학기
        String createdAt, // 생성일
        long problemCount, // 문제 수
        long likeCount, // 좋아요 수
        long dislikeCount, // 싫어요 수
        Author author // 작성자 정보
) {

    public static WorkbookProfileResponse of(
            Long id,
            String name,
            String professor,
            String examType,
            Integer coverImage,
            Integer courseYear,
            String semester,
            String createdAt,
            long problemCount,
            long likeCount,
            long dislikeCount,
            Author author
    ) {
        return new WorkbookProfileResponse(
                id,
                name,
                professor,
                examType,
                coverImage,
                courseYear,
                semester,
                createdAt,
                problemCount,
                likeCount,
                dislikeCount,
                author
        );
    }

    public static WorkbookProfileResponse from(
            Workbook workbook,
            long problemCount,
            User author
    ) {
        return new WorkbookProfileResponse(
                workbook.getId(),
                workbook.getName(),
                workbook.getProfessor(),
                workbook.getExamType().getLabel(),
                workbook.getCoverImage(),
                workbook.getCourseYear(),
                workbook.getSemester().getLabel(),
                workbook.getCreatedAt().toString(),
                problemCount,
                workbook.getLikeCount(),
                workbook.getDislikeCount(),
                Author.from(author)
        );
    }
}
