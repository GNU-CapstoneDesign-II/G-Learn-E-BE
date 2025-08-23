package gnu.capstone.G_Learn_E.domain.user.dto.response;

import java.time.LocalDate;

public record DailyActivityCountResponse(
        LocalDate date,
        long      count
) {
    public static DailyActivityCountResponse of(
            LocalDate date,
            long      count
    ) {
        return new DailyActivityCountResponse(date, count);
    }
}
