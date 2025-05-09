package gnu.capstone.G_Learn_E.domain.user.dto;

public record DepartmentRanking(
        Long id,
        String name,
        double level,
        Long createdWorkbooks,
        Long solvedWorkbooks
) {
}
