package gnu.capstone.G_Learn_E.domain.workbook.repository;

import gnu.capstone.G_Learn_E.domain.workbook.entity.Workbook;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkbookRepository extends JpaRepository<Workbook, Long> {
    @Query("SELECT swm.workbook FROM SubjectWorkbookMap swm WHERE swm.subject.id = :subjectId")
    List<Workbook> findAllBySubjectId(@Param("subjectId") Long subjectId);

    @EntityGraph(attributePaths = {
            "problemWorkbookMaps",
            "problemWorkbookMaps.problem"
    })
    Optional<Workbook> findWithMappingsAndProblemsById(@Param("workbookId") Long workbookId);
}
