package gnu.capstone.G_Learn_E.domain.public_folder.repository;

import gnu.capstone.G_Learn_E.domain.public_folder.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Optional<Department> findByNameAndCollegeId(String name, Long collegeId);
    List<Department> findByCollegeId(Long collegeId);

    Optional<Department> findByIdAndCollegeId(Long id, Long collegeId);
}

