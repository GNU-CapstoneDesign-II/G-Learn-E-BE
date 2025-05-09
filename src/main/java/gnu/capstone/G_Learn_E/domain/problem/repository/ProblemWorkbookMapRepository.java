package gnu.capstone.G_Learn_E.domain.problem.repository;

import gnu.capstone.G_Learn_E.domain.problem.entity.ProblemWorkbookMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProblemWorkbookMapRepository
        extends JpaRepository<ProblemWorkbookMap, Long> {

    @Modifying(clearAutomatically = true)          // flush + 1차캐시 sync
    @Query("DELETE FROM ProblemWorkbookMap m WHERE m.workbook.id = :wbId")
    void deleteByWorkbookId(@Param("wbId") Long workbookId);
}
