package gnu.capstone.G_Learn_E.domain.workbook.repository;

import gnu.capstone.G_Learn_E.domain.workbook.entity.Workbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkbookRepository extends JpaRepository<Workbook, Long> {
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
            "problemWorkbookMaps.problem"
    })
    Optional<Workbook> findWithMappingsAndProblemsById(@Param("workbookId") Long workbookId);

    /** 폴더·과목 매핑이 모두 없는 고아 워크북 조회 */
    @Query("""
        SELECT w FROM Workbook w
        WHERE w.folderWorkbookMaps IS EMPTY
          AND w.subjectWorkbookMaps IS EMPTY
    """)
    List<Workbook> findOrphanWorkbooks();



    // — 전체
    @EntityGraph(attributePaths = { "author" })
    @Query("SELECT w FROM Workbook w")
    Page<Workbook> findAllWorkbooks(Pageable pageable);

    // — 단과대 기준
    @EntityGraph(attributePaths = { "author" })
    @Query("""
        SELECT DISTINCT m.workbook
          FROM SubjectWorkbookMap m
         WHERE m.subject.department.college.id = :collegeId
        """)
    Page<Workbook> findAllByCollegeId(@Param("collegeId") Long collegeId, Pageable pageable);

    // — 학과 기준
    @EntityGraph(attributePaths = { "author" })
    @Query("""
        SELECT DISTINCT m.workbook
          FROM SubjectWorkbookMap m
         WHERE m.subject.department.id = :departmentId
        """)
    Page<Workbook> findAllByDepartmentId(@Param("departmentId") Long departmentId, Pageable pageable);

    // — 과목 기준
    @EntityGraph(attributePaths = { "author" })
    @Query("""
        SELECT m.workbook
          FROM SubjectWorkbookMap m
         WHERE m.subject.id = :subjectId
        """)
    Page<Workbook> findAllBySubjectId(@Param("subjectId") Long subjectId, Pageable pageable);
}
