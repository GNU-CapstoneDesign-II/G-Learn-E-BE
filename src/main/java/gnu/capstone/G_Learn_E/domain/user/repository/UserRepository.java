package gnu.capstone.G_Learn_E.domain.user.repository;

import gnu.capstone.G_Learn_E.domain.public_folder.entity.SubjectWorkbookId;
import gnu.capstone.G_Learn_E.domain.public_folder.entity.SubjectWorkbookMap;
import gnu.capstone.G_Learn_E.domain.user.entity.User;
import gnu.capstone.G_Learn_E.global.common.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
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

}

