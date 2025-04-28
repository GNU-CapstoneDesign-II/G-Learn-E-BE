package gnu.capstone.G_Learn_E.domain.problem.repository;

import gnu.capstone.G_Learn_E.domain.problem.entity.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProblemRepository extends JpaRepository<Problem, Long> {

    /** 어떤 문제집에도 속하지 않는 고아 문제 조회 */
    @Query("""
        SELECT p FROM Problem p
        WHERE p.problemWorkbookMaps IS EMPTY
    """)
    List<Problem> findOrphanProblems();
}
