package gnu.capstone.G_Learn_E.domain.public_folder.repository;

import gnu.capstone.G_Learn_E.domain.public_folder.entity.College;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CollegeRepository extends JpaRepository<College, Long> {
    Optional<College> findByName(String name);

    List<College> findAllByCollegeTrue();
    List<College> findAllByCollegeFalse();
}
