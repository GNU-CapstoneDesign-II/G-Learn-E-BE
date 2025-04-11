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
}
