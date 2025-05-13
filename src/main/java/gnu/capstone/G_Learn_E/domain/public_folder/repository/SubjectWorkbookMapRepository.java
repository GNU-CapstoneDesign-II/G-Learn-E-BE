package gnu.capstone.G_Learn_E.domain.public_folder.repository;

import gnu.capstone.G_Learn_E.domain.public_folder.entity.SubjectWorkbookId;
import gnu.capstone.G_Learn_E.domain.public_folder.entity.SubjectWorkbookMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectWorkbookMapRepository extends JpaRepository<SubjectWorkbookMap, SubjectWorkbookId> {
    List<SubjectWorkbookMap> findAllBySubject_Id(Long subjectId);

    boolean existsByWorkbook_Id(Long workbookId);
}
