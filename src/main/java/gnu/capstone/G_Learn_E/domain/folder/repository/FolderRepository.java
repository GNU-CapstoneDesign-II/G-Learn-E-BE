package gnu.capstone.G_Learn_E.domain.folder.repository;

import gnu.capstone.G_Learn_E.domain.folder.entity.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {
}
