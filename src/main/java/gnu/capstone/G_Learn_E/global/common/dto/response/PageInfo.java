package gnu.capstone.G_Learn_E.global.common.dto.response;

public record PageInfo(
        Long totalElements,
        Integer totalPages,
        Integer pageNumber,
        boolean hasNextPage,
        boolean hasPreviousPage
) {
    public static PageInfo of(
            Long totalElements,
            Integer totalPages,
            Integer pageNumber,
            boolean hasNextPage,
            boolean hasPreviousPage
    ) {
        return new PageInfo(totalElements, totalPages, pageNumber, hasNextPage, hasPreviousPage);
    }
}
