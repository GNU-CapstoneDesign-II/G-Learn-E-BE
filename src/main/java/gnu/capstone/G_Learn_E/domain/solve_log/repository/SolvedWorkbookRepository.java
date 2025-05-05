package gnu.capstone.G_Learn_E.domain.solve_log.repository;

import gnu.capstone.G_Learn_E.domain.solve_log.entity.SolvedWorkbook;
import gnu.capstone.G_Learn_E.domain.solve_log.entity.SolvedWorkbookId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolvedWorkbookRepository extends JpaRepository<SolvedWorkbook, SolvedWorkbookId> {
}
