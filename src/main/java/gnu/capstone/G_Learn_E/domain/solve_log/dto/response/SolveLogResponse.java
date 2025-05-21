package gnu.capstone.G_Learn_E.domain.solve_log.dto.response;

import java.time.LocalDateTime;

public record SolveLogResponse(
        String status, // NOT_STARTED("기록 없음"),IN_PROGRESS("풀이중"),COMPLETED("채점 완료");
        LocalDateTime recentDateTime, // 최근 풀이 시간
        int correctCount, // 정답 수
        int wrongCount // 오답 수
) {
    public static SolveLogResponse of(
            String status,
            LocalDateTime recentDateTime,
            int correctCount,
            int wrongCount
    ) {
        return new SolveLogResponse(
                status,
                recentDateTime,
                correctCount,
                wrongCount
        );
    }
}
