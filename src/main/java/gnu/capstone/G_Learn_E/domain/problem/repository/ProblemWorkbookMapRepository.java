package gnu.capstone.G_Learn_E.domain.problem.repository;

import gnu.capstone.G_Learn_E.domain.problem.entity.ProblemWorkbookMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProblemWorkbookMapRepository
        extends JpaRepository<ProblemWorkbookMap, Long> {

    /**
     * workbook.id 를 기준으로
     * problemNumber 순 정렬된 매핑 엔티티 리스트를 가져옵니다.
     */
    List<ProblemWorkbookMap> findAllByWorkbook_IdOrderByProblemNumber(Long workbookId);
}
