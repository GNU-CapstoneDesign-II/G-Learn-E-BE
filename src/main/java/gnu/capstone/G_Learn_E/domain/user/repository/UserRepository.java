package gnu.capstone.G_Learn_E.domain.user.repository;

import gnu.capstone.G_Learn_E.domain.public_folder.entity.SubjectWorkbookId;
import gnu.capstone.G_Learn_E.domain.public_folder.entity.SubjectWorkbookMap;
import gnu.capstone.G_Learn_E.domain.user.dto.CollegeRanking;
import gnu.capstone.G_Learn_E.domain.user.dto.DepartmentRanking;
import gnu.capstone.G_Learn_E.domain.user.dto.response.DepartmentRankingPageResponse;
import gnu.capstone.G_Learn_E.domain.user.entity.User;
import gnu.capstone.G_Learn_E.global.common.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Optional<User> findUserByEmail(String email);

    Optional<User> findById(Long id);
    List<User> findAllByIdIn(List<Long> ids);


    /**
     * 특정 레벨·경험치 기준으로 더 높은 랭킹(레벨이 높거나,
     * 같은 레벨에서 경험치가 더 많은)인 유저 수를 반환합니다.
     */
    @Query("SELECT COUNT(u) FROM User u " +
            " WHERE u.level > :level " +
            "    OR (u.level = :level AND u.exp > :exp)")
    long countHigherRanked(
            @Param("level") Short level,
            @Param("exp")   Integer exp
    );



    @Query("""
        SELECT new gnu.capstone.G_Learn_E.domain.user.dto.DepartmentRanking(
            u.department.id,
            u.department.name,
            AVG(u.level),
            COUNT(sw),
            SUM(u.createWorkbookCount)
        )
        FROM User u
        LEFT JOIN u.solvedWorkbooks sw
        WHERE u.department.college.college = true
        GROUP BY u.department.id, u.department.name
        ORDER BY
          AVG(u.level) DESC
        """)
    List<DepartmentRanking> findDepartmentRankingsByLevel(Pageable pageable);

    @Query("""
        SELECT new gnu.capstone.G_Learn_E.domain.user.dto.DepartmentRanking(
            u.department.id,
            u.department.name,
            AVG(u.level),
            COUNT(sw),
            SUM(u.createWorkbookCount)
        )
        FROM User u
        LEFT JOIN u.solvedWorkbooks sw
        WHERE u.department.college.college = true
        GROUP BY u.department.id, u.department.name
        ORDER BY
          COUNT(sw) DESC
        """)
    List<DepartmentRanking> findDepartmentRankingsByTotalSolved(Pageable pageable);

    @Query("""
        SELECT new gnu.capstone.G_Learn_E.domain.user.dto.DepartmentRanking(
            u.department.id,
            u.department.name,
            AVG(u.level),
            COUNT(sw),
            SUM(u.createWorkbookCount)
        )
        FROM User u
        LEFT JOIN u.solvedWorkbooks sw
        WHERE u.department.college.college = true
        GROUP BY u.department.id, u.department.name
        ORDER BY
        SUM(u.createWorkbookCount) DESC
        """)
    List<DepartmentRanking> findDepartmentRankingsByTotalCreated(Pageable pageable);




    @Query("""
        SELECT new gnu.capstone.G_Learn_E.domain.user.dto.CollegeRanking(
            u.college.id,
            u.college.name,
            AVG(u.level),
            COUNT(sw),
            SUM(u.createWorkbookCount)
        )
        FROM User u
        LEFT JOIN u.solvedWorkbooks sw
        WHERE u.college.college = true
        GROUP BY u.college.id, u.college.name
        ORDER BY
          AVG(u.level) DESC
        """)
    List<CollegeRanking> findCollegeRankingsByLevel(Pageable pageable);

    @Query("""
        SELECT new gnu.capstone.G_Learn_E.domain.user.dto.CollegeRanking(
            u.college.id,
            u.college.name,
            AVG(u.level),
            COUNT(sw),
            SUM(u.createWorkbookCount)
        )
        FROM User u
        LEFT JOIN u.solvedWorkbooks sw
        WHERE u.college.college = true
        GROUP BY u.college.id, u.college.name
        ORDER BY
          COUNT(sw) DESC
        """)
    List<CollegeRanking> findCollegeRankingsByTotalSolved(Pageable pageable);

    @Query("""
        SELECT new gnu.capstone.G_Learn_E.domain.user.dto.CollegeRanking(
            u.college.id,
            u.college.name,
            AVG(u.level),
            COUNT(sw),
            SUM(u.createWorkbookCount)
        )
        FROM User u
        LEFT JOIN u.solvedWorkbooks sw
        WHERE u.college.college = true
        GROUP BY u.college.id, u.college.name
        ORDER BY
        SUM(u.createWorkbookCount) DESC
        """)
    List<CollegeRanking> findCollegeRankingsByTotalCreated(Pageable pageable);

    Page<User> findByDepartmentIdOrderByLevelDescExpDesc(Long departmentId, Pageable pageable);

    Page<User> findByDepartmentIdOrderBySolvedWorkbookCountDesc(Long departmentId, Pageable pageable);

    Page<User> findByDepartmentIdOrderByCreateWorkbookCountDesc(Long departmentId, Pageable pageable);

    Page<User> findByCollegeIdOrderByLevelDescExpDesc(Long collegeId, Pageable pageable);

    Page<User> findByCollegeIdOrderBySolvedWorkbookCountDesc(Long collegeId, Pageable pageable);

    Page<User> findByCollegeIdOrderByCreateWorkbookCountDesc(Long collegeId, Pageable pageable);
}

