package gnu.capstone.G_Learn_E.domain.public_folder.repository;

import gnu.capstone.G_Learn_E.domain.public_folder.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    List<Department> findByCollegeId(Long collegeId);
}

