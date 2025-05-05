package gnu.capstone.G_Learn_E.domain.solve_log.repository;

import gnu.capstone.G_Learn_E.domain.solve_log.entity.SolveLog;
import gnu.capstone.G_Learn_E.domain.solve_log.entity.SolvedWorkbookId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolveLogRepository extends JpaRepository<SolveLog, Long> {

    List<SolveLog> findAllBySolvedWorkbookId(SolvedWorkbookId solvedWorkbook_id);
}
