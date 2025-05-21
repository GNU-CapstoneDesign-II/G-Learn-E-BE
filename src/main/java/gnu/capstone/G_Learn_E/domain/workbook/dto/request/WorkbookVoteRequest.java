package gnu.capstone.G_Learn_E.domain.workbook.dto.request;

import gnu.capstone.G_Learn_E.domain.workbook.enums.WorkbookVoteType;

public record WorkbookVoteRequest(
        WorkbookVoteType voteType
) {
}
