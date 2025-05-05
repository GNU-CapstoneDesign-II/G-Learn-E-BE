package gnu.capstone.G_Learn_E.domain.problem.dto.response;

import gnu.capstone.G_Learn_E.domain.problem.entity.ProblemWorkbookMap;
import gnu.capstone.G_Learn_E.global.common.serialization.Option;

import java.util.Comparator;
import java.util.List;


public record ProblemResponse(
        Long id,               // 문제 ID
        Integer problemNumber, // 매핑테이블에서 가져오는 문제 번호
        String type,           // 문제 유형
        String title,          // 문제 제목
        List<String> options,  // 객관식 보기 (Option.content)
        List<String> answers,  // 정답 리스트
        String explanation     // 해설
) {
    /** 매핑 엔티티로부터 DTO 생성 */
    public static ProblemResponse from(ProblemWorkbookMap map) {
        var p = map.getProblem();
        return new ProblemResponse(
                p.getId(),
                map.getProblemNumber(),
                p.getType().name(),
                p.getTitle(),
                p.getOptions() == null
                        ? List.of()
                        : p.getOptions().stream()
                        .map(Option::getContent)
                        .toList(),
                p.getAnswers(),
                p.getExplanation()
        );
    }

    /** 정렬된 매핑 리스트에서 한 번에 변환 */
    public static List<ProblemResponse> from(List<ProblemWorkbookMap> maps) {
        return maps.stream()
                .sorted(Comparator.comparing(ProblemWorkbookMap::getProblemNumber))
                .map(ProblemResponse::from)
                .toList();
    }
}