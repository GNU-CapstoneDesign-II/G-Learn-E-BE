package gnu.capstone.G_Learn_E.domain.problem.dto.response;

public record KeywordFrequencyResponse(
        String keyword,
        Long count
) {
    public static KeywordFrequencyResponse of(String keyword, Long count) {
        return new KeywordFrequencyResponse(keyword, count);
    }
}
