package gnu.capstone.G_Learn_E.domain.solve_log.repository;

import gnu.capstone.G_Learn_E.domain.problem.entity.Problem;
import gnu.capstone.G_Learn_E.domain.solve_log.entity.SolveLog;
import gnu.capstone.G_Learn_E.domain.solve_log.entity.SolvedWorkbook;
import gnu.capstone.G_Learn_E.domain.solve_log.entity.SolvedWorkbookId;
import gnu.capstone.G_Learn_E.domain.solve_log.enums.SolvingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolveLogRepository extends JpaRepository<SolveLog, Long> {

    List<SolveLog> findAllBySolvedWorkbookId(SolvedWorkbookId solvedWorkbook_id);

    List<SolveLog> findBySolvedWorkbookAndProblemIn(SolvedWorkbook solvedWorkbook, List<Problem> problems);


    @Query("""
        SELECT sl FROM SolveLog sl
            JOIN FETCH sl.problem p
        WHERE sl.solvedWorkbook.user.id = :userId
         AND sl.solvedWorkbook.status = :status
    """)
    List<SolveLog> findAllCompletedSolveLogs(
            @Param("userId") Long userId,
            @Param("status") SolvingStatus status
    );
}
