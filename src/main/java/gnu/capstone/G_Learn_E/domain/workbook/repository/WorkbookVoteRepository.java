package gnu.capstone.G_Learn_E.domain.workbook.repository;

import gnu.capstone.G_Learn_E.domain.workbook.entity.WorkbookVote;
import gnu.capstone.G_Learn_E.domain.workbook.enums.WorkbookVoteType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkbookVoteRepository extends JpaRepository<WorkbookVote, Long> {

    Optional<WorkbookVote> findByUserIdAndWorkbookId(Long userId, Long workbookId);

    Long countByWorkbookIdAndVoteType(Long workbook_id, WorkbookVoteType voteType);
}
