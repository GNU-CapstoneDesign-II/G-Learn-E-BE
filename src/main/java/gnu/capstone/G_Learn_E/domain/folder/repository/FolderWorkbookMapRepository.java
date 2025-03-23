package gnu.capstone.G_Learn_E.domain.folder.repository;

import gnu.capstone.G_Learn_E.domain.folder.entity.FolderWorkbookMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FolderWorkbookMapRepository extends JpaRepository<FolderWorkbookMap, Long> {
}
