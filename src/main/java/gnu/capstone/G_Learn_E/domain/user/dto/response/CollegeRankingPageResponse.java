package gnu.capstone.G_Learn_E.domain.user.dto.response;

import gnu.capstone.G_Learn_E.global.common.dto.response.PageInfo;

import java.util.List;

public record CollegeRankingPageResponse(
    PageInfo pageInfo,
    List<CollegeRankingResponse> rankings
) {

    public static CollegeRankingPageResponse from(
            PageInfo pageInfo,
            List<CollegeRankingResponse> rankings
    ) {
        return new CollegeRankingPageResponse(pageInfo, rankings);
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
