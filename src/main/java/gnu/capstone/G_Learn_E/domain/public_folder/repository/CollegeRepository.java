package gnu.capstone.G_Learn_E.domain.public_folder.repository;

import gnu.capstone.G_Learn_E.domain.public_folder.entity.College;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CollegeRepository extends JpaRepository<College, Long> {
}
