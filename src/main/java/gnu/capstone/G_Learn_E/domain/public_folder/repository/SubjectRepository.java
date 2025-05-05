package gnu.capstone.G_Learn_E.domain.public_folder.repository;

import gnu.capstone.G_Learn_E.domain.public_folder.entity.Subject;
import gnu.capstone.G_Learn_E.domain.public_folder.enums.SubjectGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    boolean existsByNameAndDepartmentId(String name, Long departmentId);
    boolean existsByNameAndGradeAndDepartmentId(String name, SubjectGrade grade, Long departmentId);
    List<Subject> findByDepartmentId(Long departmentId);

    Optional<Subject> findByIdAndDepartmentId(Long id, Long departmentId);
}
