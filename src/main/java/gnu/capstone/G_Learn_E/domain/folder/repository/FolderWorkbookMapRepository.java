package gnu.capstone.G_Learn_E.domain.folder.repository;

import gnu.capstone.G_Learn_E.domain.folder.entity.Folder;
import gnu.capstone.G_Learn_E.domain.folder.entity.FolderWorkbookId;
import gnu.capstone.G_Learn_E.domain.folder.entity.FolderWorkbookMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FolderWorkbookMapRepository extends JpaRepository<FolderWorkbookMap, FolderWorkbookId> {

    List<FolderWorkbookMap> findByFolder(Folder folder);


    @Query("""
        SELECT m FROM FolderWorkbookMap m
        JOIN FETCH m.workbook
        WHERE m.folder = :folder
    """)
    List<FolderWorkbookMap> findByFolderWithWorkbook(@Param("folder") Folder folder);

    Optional<FolderWorkbookMap> findByWorkbook_Id(Long workbookId);
}
