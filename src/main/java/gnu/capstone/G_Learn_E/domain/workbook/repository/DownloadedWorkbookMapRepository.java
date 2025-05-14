package gnu.capstone.G_Learn_E.domain.workbook.repository;

import gnu.capstone.G_Learn_E.domain.user.entity.User;
import gnu.capstone.G_Learn_E.domain.workbook.entity.DownloadedWorkbookMap;
import gnu.capstone.G_Learn_E.domain.workbook.entity.Workbook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DownloadedWorkbookMapRepository extends JpaRepository<DownloadedWorkbookMap, Long> {

    boolean existsByUserAndWorkbook(User user, Workbook workbook);

    boolean existsByUserIdAndWorkbookId(Long userId, Long workbookId);

    List<DownloadedWorkbookMap> findAllByUserId(Long userId);
}
