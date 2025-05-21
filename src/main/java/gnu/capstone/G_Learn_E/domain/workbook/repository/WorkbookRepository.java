package gnu.capstone.G_Learn_E.domain.workbook.repository;

import gnu.capstone.G_Learn_E.domain.user.entity.User;
import gnu.capstone.G_Learn_E.domain.workbook.entity.Workbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkbookRepository extends JpaRepository<Workbook, Long>, JpaSpecificationExecutor<Workbook> {
    @Query("""
        select distinct w
        from SubjectWorkbookMap swm
        join swm.workbook w
        join fetch w.author
        where swm.subject.id = :subjectId
    """)
    List<Workbook> findAllWithAuthorBySubjectId(@Param("subjectId") Long subjectId);

    @EntityGraph(attributePaths = {
            "problemWorkbookMaps",
            "problemWorkbookMaps.problem",
            "author"
    })
    Optional<Workbook> findWithMappingsAndProblemsById(@Param("workbookId") Long workbookId);

    /** 폴더·과목 매핑이 모두 없는 고아 워크북 조회 */
    @Query("""
        SELECT w FROM Workbook w
        WHERE w.folderWorkbookMaps IS EMPTY
          AND w.subjectWorkbookMaps IS EMPTY
    """)
    List<Workbook> findOrphanWorkbooks();



    /** 전체 워크북 조회 (Subject 매핑만 타고 들어가도록) */
    @EntityGraph(attributePaths = { "author" })
    @Query("""
        SELECT DISTINCT w
          FROM Workbook w
          JOIN w.subjectWorkbookMaps m
         WHERE w.author.id NOT IN (
             SELECT ub.targetUser.id
               FROM UserBlacklist ub
              WHERE ub.user.id = :userId
         )
    """)
    Page<Workbook> findAllWorkbooks(Pageable pageable, @Param("userId") Long userId);

    /** 단과대별 워크북 조회 */
    @EntityGraph(attributePaths = { "author" })
    @Query("""
        SELECT DISTINCT w
          FROM Workbook w
          JOIN w.subjectWorkbookMaps m
         WHERE m.subject.department.college.id = :collegeId
         AND w.author.id NOT IN (
             SELECT ub.targetUser.id
               FROM UserBlacklist ub
              WHERE ub.user.id = :userId
         )
    """)
    Page<Workbook> findAllByCollegeId(@Param("collegeId") Long collegeId, Pageable pageable, @Param("userId") Long userId);

    /** 학과별 워크북 조회 */
    @EntityGraph(attributePaths = { "author" })
    @Query("""
        SELECT DISTINCT w
          FROM Workbook w
          JOIN w.subjectWorkbookMaps m
         WHERE m.subject.department.id = :departmentId
         AND w.author.id NOT IN (
             SELECT ub.targetUser.id
               FROM UserBlacklist ub
              WHERE ub.user.id = :userId
         )
    """)
    Page<Workbook> findAllByDepartmentId(@Param("departmentId") Long departmentId, Pageable pageable, @Param("userId") Long userId);

    /** 과목별 워크북 조회 */
    @EntityGraph(attributePaths = { "author" })
    @Query("""
        SELECT DISTINCT w
          FROM Workbook w
          JOIN w.subjectWorkbookMaps m
         WHERE m.subject.id = :subjectId
         AND w.author.id NOT IN (
             SELECT ub.targetUser.id
               FROM UserBlacklist ub
              WHERE ub.user.id = :userId
         )
    """)
    Page<Workbook> findAllBySubjectId(@Param("subjectId") Long subjectId, Pageable pageable, @Param("userId") Long userId);


    @EntityGraph(attributePaths = { "author" })
    List<Workbook> findAllById(Iterable<Long> ids);

    @EntityGraph(attributePaths = { "author" })           // ← join fetch author
    Page<Workbook> findAll(Specification<Workbook> spec,
                           Pageable pageable);

    @Query(value = """
    SELECT  w.*,
            ( (MATCH(w.name)         AGAINST (:kw IN BOOLEAN MODE)) * 4
            + (MATCH(u.nickname)     AGAINST (:kw IN BOOLEAN MODE)) * 3
            + IFNULL(MAX(
                   MATCH(p.title, p.explanation, p.options, p.answers)
                   AGAINST (:kw IN BOOLEAN MODE)
              ) * 2, 0) ) AS score
    FROM workbook w
    JOIN user u  ON u.id = w.author_id
    LEFT JOIN problem_workbook_map pwm ON pwm.workbook_id = w.id
    LEFT JOIN problem p ON p.id = pwm.problem_id
    /* ---- 키워드 ---- */
    WHERE ( MATCH(w.name)        AGAINST (:kw IN BOOLEAN MODE)
         OR MATCH(u.nickname)    AGAINST (:kw IN BOOLEAN MODE)
         OR MATCH(p.title, p.explanation, p.options, p.answers)
                                  AGAINST (:kw IN BOOLEAN MODE) )
    /* ---- 범위 ---- */
    AND  ( (:range = 'public'  AND EXISTS (SELECT 1 FROM subject_workbook_map sw
                                           WHERE sw.workbook_id = w.id))
       OR  (:range = 'private' AND NOT EXISTS (SELECT 1 FROM subject_workbook_map sw
                                               WHERE sw.workbook_id = w.id)
                               AND w.author_id = :currentUserId) )
    GROUP BY w.id
    ORDER BY score DESC
    """,
                countQuery = """
    SELECT COUNT(DISTINCT w.id)
    FROM workbook w
    JOIN user u  ON u.id = w.author_id
    LEFT JOIN problem_workbook_map pwm ON pwm.workbook_id = w.id
    LEFT JOIN problem p ON p.id = pwm.problem_id
    WHERE ( MATCH(w.name)        AGAINST (:kw IN BOOLEAN MODE)
         OR MATCH(u.nickname)    AGAINST (:kw IN BOOLEAN MODE)
         OR MATCH(p.title, p.explanation, p.options, p.answers)
                                  AGAINST (:kw IN BOOLEAN MODE) )
    AND  ( (:range = 'public'  AND EXISTS (SELECT 1 FROM subject_workbook_map sw
                                           WHERE sw.workbook_id = w.id))
       OR  (:range = 'private' AND NOT EXISTS (SELECT 1 FROM subject_workbook_map sw
                                               WHERE sw.workbook_id = w.id)
                               AND w.author_id = :currentUserId) )
    """,
            nativeQuery = true)
    Page<Workbook> searchByRelevance(
            @Param("kw")    String keyword,
            @Param("range") String range,
            @Param("currentUserId") Long currentUserId,
            Pageable pageable
    );

    @Query("""
        SELECT DISTINCT w
          FROM Workbook w
          JOIN w.problemWorkbookMaps pwm
          JOIN pwm.problem p
          JOIN p.problemKeywords pk
          LEFT JOIN w.folderWorkbookMaps fwm
          LEFT JOIN w.subjectWorkbookMaps swm
         WHERE pk.keyword = :keyword
           AND (
               fwm.folder.user.id = :userId
               OR swm IS NOT NULL
           )
    """)
    @EntityGraph(attributePaths = {
            "problemWorkbookMaps",
            "problemWorkbookMaps.problem",
            "problemWorkbookMaps.problem.problemKeywords"
    })
    List<Workbook> findAccessibleByKeyword(
            @Param("keyword") String keyword,
            @Param("userId")  Long userId
    );
}
