package gnu.capstone.G_Learn_E.domain.public_folder.dto.response;

import gnu.capstone.G_Learn_E.global.common.dto.response.PageInfo;
import gnu.capstone.G_Learn_E.global.common.dto.response.PublicWorkbook;

import java.util.List;

public record PublicWorkbookSearchResponse(
        PageInfo pageInfo,
        List<PublicWorkbook> publicWorkbooks
) {
    public static PublicWorkbookSearchResponse from(
            PageInfo pageInfo,
            List<PublicWorkbook> publicWorkbooks
    ) {
        return new PublicWorkbookSearchResponse(pageInfo, publicWorkbooks);
    }
}
