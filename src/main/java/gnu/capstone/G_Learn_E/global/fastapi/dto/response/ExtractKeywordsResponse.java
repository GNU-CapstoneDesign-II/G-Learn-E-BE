package gnu.capstone.G_Learn_E.global.fastapi.dto.response;

import java.util.List;

public record ExtractKeywordsResponse(
        List<ProblemKeyword> problems,
        int requestTokens,
        int responseTokens,
        float estimatedCostKrw
) {
    public record ProblemKeyword(
            Long id,
            List<String> keywords
    ) {
    }
}
