package gnu.capstone.G_Learn_E.domain.folder.repository;

import gnu.capstone.G_Learn_E.domain.folder.entity.Folder;
import gnu.capstone.G_Learn_E.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {
    // MEMO: JPA 트러블슈팅 메모!!
    // N+1 문제를 최소화하기 위해 Folder.children은 fetch join (LEFT JOIN FETCH)으로 가져옴
    // Folder.folderWorkbookMaps는 LAZY 로딩으로 가져옴
    // → Hibernate는 2개 이상의 List(bag)를 동시에 fetch join할 수 없기 때문에,
    //    children만 조인하고, workbook 쪽은 필요할 때 Lazy로 한 번만 조회되도록 처리함
    // children, workbook 둘 중에 뭘 join해서 가져올지는 성능 분석 필요할듯.
    // (일반적으로 폴더 개수보다 문제집 개수가 더 많으니 workbook 쪽을 join해서 가져오는게 더 나을듯...? 나중에 공부해보고 수정해보기)


    Optional<Folder> findByUserAndParentIsNull(User user);

    @Query("""
        SELECT f FROM Folder f
        LEFT JOIN FETCH f.children c
        WHERE f.user = :user AND f.parent IS NULL
    """)
    Optional<Folder> findByUserAndParentIsNullWithChildren(@Param("user") User user);

    @Query("""
        SELECT DISTINCT f FROM Folder f
        LEFT JOIN FETCH f.children
        WHERE f.id = :id
    """)
    Optional<Folder> findByIdWithChildren(@Param("id") Long id);


    List<Folder> findByParentAndUser(Folder parent, User user);


    @Query("""
        SELECT f FROM Folder f
        LEFT JOIN FETCH f.parent
        WHERE f.user = :user
    """)
    List<Folder> findAllByUserWithParent(@Param("user") User user); // 폴더 트리 조회

}
