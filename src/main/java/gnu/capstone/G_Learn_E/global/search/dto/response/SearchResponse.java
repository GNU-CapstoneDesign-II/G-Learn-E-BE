package gnu.capstone.G_Learn_E.global.search.dto.response;

import gnu.capstone.G_Learn_E.domain.workbook.entity.Workbook;
import gnu.capstone.G_Learn_E.global.common.dto.response.PrivateWorkbook;
import gnu.capstone.G_Learn_E.global.common.dto.response.PublicPath;
import gnu.capstone.G_Learn_E.global.common.dto.response.PublicWorkbook;

import java.util.List;
import java.util.Map;
import java.util.Set;

public record SearchResponse(
        List<PrivateWorkbook> privateWorkbooks,
        List<PublicWorkbook> publicWorkbooks
) {

    public static SearchResponse from(
            List<Workbook> privateWorkbooks,
            List<Workbook> publicWorkbooks,
            Set<Long> usersDownloaded,
            Map<Long, List<PublicPath>> publicWorkbookPaths
    ) {
        return new SearchResponse(
                privateWorkbooks == null ? null :
                        privateWorkbooks.stream()
                                .map(PrivateWorkbook::from)
                                .toList(),
                publicWorkbooks == null ? null :
                        publicWorkbooks.stream()
                                .map(workbook -> PublicWorkbook.from(
                                        workbook, usersDownloaded.contains(workbook.getId()),
                                        publicWorkbookPaths.get(workbook.getId())
                                ))
                                .toList()
        );
    }
}