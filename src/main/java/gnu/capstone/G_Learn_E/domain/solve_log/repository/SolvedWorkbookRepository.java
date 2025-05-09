package gnu.capstone.G_Learn_E.domain.solve_log.repository;

import gnu.capstone.G_Learn_E.domain.solve_log.entity.SolvedWorkbook;
import gnu.capstone.G_Learn_E.domain.solve_log.entity.SolvedWorkbookId;
import gnu.capstone.G_Learn_E.domain.solve_log.enums.SolvingStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SolvedWorkbookRepository
        extends JpaRepository<SolvedWorkbook, SolvedWorkbookId> {

    /**
     * 기본 findById 메서드를 오버라이드하되,
     * EntityGraph를 붙여 solveLogs만 즉시 로딩하도록 합니다.
     */
    @Override
    @EntityGraph(attributePaths = "solveLogs")
    Optional<SolvedWorkbook> findById(SolvedWorkbookId id);


    /**
     * 문제집 ID로 푼 문제집을 찾습니다.
     * @param workbookId 문제집 ID
     * @return 푼 문제집
     */
    @EntityGraph(attributePaths = "solveLogs")
    List<SolvedWorkbook> findAllByWorkbookId(Long workbookId);


    /**
     * 특정 유저가 완료(COMPLETED)한 SolvedWorkbook 개수를 반환합니다.
     * @param userId 조회할 유저 ID
     * @param status 상태 (여기서는 SolvingStatus.COMPLETED)
     * @return 완료된 워크북 개수
     */
    long countByIdUserIdAndStatus(Long userId, SolvingStatus status);
}