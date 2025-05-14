package gnu.capstone.G_Learn_E.domain.user.dto.response;

public record UserStatisticsResponse(
        long ranking,
        long createdWorkbooks,
        long solvedWorkbooks,
        long uploadedWorkbooks
) {

    public static UserStatisticsResponse of(
            long ranking,
            long createdWorkbooks,
            long solvedWorkbooks,
            long uploadedWorkbooks
    ) {
        return new UserStatisticsResponse(ranking, createdWorkbooks, solvedWorkbooks, uploadedWorkbooks);
    }
}
