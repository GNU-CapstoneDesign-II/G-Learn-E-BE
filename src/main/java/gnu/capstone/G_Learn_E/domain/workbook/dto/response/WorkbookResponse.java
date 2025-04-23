package gnu.capstone.G_Learn_E.domain.workbook.dto.response;

import gnu.capstone.G_Learn_E.domain.problem.dto.response.ProblemResponse;
import gnu.capstone.G_Learn_E.domain.workbook.entity.Workbook;

import java.util.List;

public record WorkbookResponse(
        Long id, // 문제집 ID
        String name, // 워크북 이름
        String professor, // 교수 이름
        String examType, // 시험 유형
        Integer coverImage, // 표지 이미지
        Integer courseYear, // 수강 연도
        String semester, // 학기
        String createdAt, // 생성일
        List<ProblemResponse> problems // 문제 목록
) {
    public static WorkbookResponse of(
            Long id,
            String name,
            String professor,
            String examType,
            Integer coverImage,
            Integer courseYear,
            String semester,
            String createdAt,
            List<ProblemResponse> problems
    ) {
        return new WorkbookResponse(id, name, professor, examType, coverImage, courseYear, semester, createdAt, problems);
    }

    public static WorkbookResponse of(Workbook workbook){
        return new WorkbookResponse(
                workbook.getId(),
                workbook.getName(),
                workbook.getProfessor(),
                workbook.getExamType().name(),
                workbook.getCoverImage(),
                workbook.getCourseYear(),
                workbook.getSemester().name(),
                workbook.getCreatedAt().toString(),
                ProblemResponse.from(workbook.getProblems())
        );
    }
}
