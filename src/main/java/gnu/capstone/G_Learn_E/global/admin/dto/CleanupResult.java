package gnu.capstone.G_Learn_E.global.admin.dto;

/** 고아 엔티티 정리 결과 */
public record CleanupResult(
        int deletedWorkbooks,
        int deletedProblems
) {

}