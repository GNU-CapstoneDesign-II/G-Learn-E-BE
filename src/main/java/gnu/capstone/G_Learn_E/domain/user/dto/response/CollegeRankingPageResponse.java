package gnu.capstone.G_Learn_E.domain.user.dto.response;

import java.util.List;

public record CollegeRankingPageResponse(
    List<CollegeRankingResponse> rankings
) {

    public static CollegeRankingPageResponse from(
            List<CollegeRankingResponse> rankings
    ) {
        return new CollegeRankingPageResponse(rankings);
    }

    public record CollegeRankingResponse(
            Long id,
            String name,
            Long ranking,
            Long level,
            Long createdWorkbooks,
            Long solvedWorkbooks
    ) {
        public static CollegeRankingResponse of(
                Long id,
                String name,
                Long ranking,
                Long level,
                Long createdWorkbooks,
                Long solvedWorkbooks
        ) {
            return new CollegeRankingResponse(id, name, ranking, level, createdWorkbooks, solvedWorkbooks);
        }
    }
}
