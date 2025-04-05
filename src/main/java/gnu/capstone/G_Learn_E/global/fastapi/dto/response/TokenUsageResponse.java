package gnu.capstone.G_Learn_E.global.fastapi.dto.response;

public record TokenUsageResponse(
        Long requestTokens,
        Long responseTokens,
        Double costUsd,
        Double costWon
) {
}
