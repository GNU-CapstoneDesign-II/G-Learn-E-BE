package gnu.capstone.G_Learn_E.domain.workbook.dto.response;

import gnu.capstone.G_Learn_E.domain.workbook.entity.Workbook;

public record WorkbookSimpleResponse(
        Long id, // 문제집 ID
        String name, // 워크북 이름
        String professor, // 교수 이름
        String examType, // 시험 유형
        Integer coverImage, // 표지 이미지
        Integer courseYear, // 수강 연도
        String semester, // 학기
        String createdAt, // 생성일
        long likeCount, // 좋아요 수
        long dislikeCount // 싫어요 수
) {
    public static WorkbookSimpleResponse of(
            Long id,
            String name,
            String professor,
            String examType,
            Integer coverImage,
            Integer courseYear,
            String semester,
            String createdAt,
            long likeCount,
            long dislikeCount
    ) {
        return new WorkbookSimpleResponse(
                id,
                name,
                professor,
                examType,
                coverImage,
                courseYear,
                semester,
                createdAt,
                likeCount,
                dislikeCount
        );
    }
    public static WorkbookSimpleResponse from(
            Workbook workbook
    ) {
        return new WorkbookSimpleResponse(
                workbook.getId(),
                workbook.getName(),
                workbook.getProfessor(),
                workbook.getExamType().name(),
                workbook.getCoverImage(),
                workbook.getCourseYear(),
                workbook.getSemester().name(),
                workbook.getCreatedAt().toString(),
                workbook.getLikeCount(),
                workbook.getDislikeCount()
        );
    }
}
