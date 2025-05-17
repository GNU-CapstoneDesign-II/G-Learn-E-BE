package gnu.capstone.G_Learn_E.domain.user.dto.response;

import gnu.capstone.G_Learn_E.global.common.dto.response.PageInfo;

import java.util.List;

public record DepartmentRankingPageResponse(
        PageInfo pageInfo,
        List<DepartmentRankingResponse> rankings
) {

    public static DepartmentRankingPageResponse from(
            PageInfo pageInfo,
            List<DepartmentRankingResponse> rankings
    ) {
        return new DepartmentRankingPageResponse(pageInfo, rankings);
    }


    public record DepartmentRankingResponse(
            Long id,
            String name,
            Long ranking,
            Long level,
            Long createdWorkbooks,
            Long solvedWorkbooks
    ) {
        public static DepartmentRankingResponse of(
                Long id,
                String name,
                Long ranking,
                Long level,
                Long createdWorkbooks,
                Long solvedWorkbooks
        ) {
            return new DepartmentRankingResponse(id, name, ranking, level, createdWorkbooks, solvedWorkbooks);
        }
    }
}
