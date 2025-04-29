package gnu.capstone.G_Learn_E.domain.problem.dto.request;

import java.util.List;

public record ProblemRequest(
        Long id,               // 문제 ID
        String type,           // 문제 유형
        String title,          // 문제 제목
        List<String> options,  // 객관식 보기 (Option.content)
        List<String> answers,  // 정답 리스트
        String explanation    // 해설
) {

}