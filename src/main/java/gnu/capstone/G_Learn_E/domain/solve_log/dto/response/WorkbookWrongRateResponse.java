package gnu.capstone.G_Learn_E.domain.solve_log.dto.response;

public record WorkbookWrongRateResponse(
        Long   workbookId,
        String name,
        double wrongRate,
        long   wrongCount,
        long   totalCount
) {
    public static WorkbookWrongRateResponse of(
            Long   workbookId,
            String name,
            double wrongRate,
            long   wrongCount,
            long   totalCount
    ) {
        return new WorkbookWrongRateResponse(
                workbookId,
                name,
                wrongRate,
                wrongCount,
                totalCount
        );
    }
}
