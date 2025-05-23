package gnu.capstone.G_Learn_E.domain.problem.repository;

import gnu.capstone.G_Learn_E.domain.problem.entity.FailedKeywordGenerate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FailedKeywordGenerateRepository extends JpaRepository<FailedKeywordGenerate, Long> {
}
