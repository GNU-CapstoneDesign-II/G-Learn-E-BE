package gnu.capstone.G_Learn_E.domain.problem.repository;

import gnu.capstone.G_Learn_E.domain.problem.entity.ProblemKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProblemKeywordRepository extends JpaRepository<ProblemKeyword, Long> {

    List<ProblemKeyword> findAllByProblem_IdIn(List<Long> problemIds);
}
