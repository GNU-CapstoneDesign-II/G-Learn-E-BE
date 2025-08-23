package gnu.capstone.G_Learn_E.domain.user.repository;

import gnu.capstone.G_Learn_E.domain.user.entity.UserBlacklist;
import gnu.capstone.G_Learn_E.domain.user.enums.BlacklistType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserBlacklistRepository extends JpaRepository<UserBlacklist, Long> {

    boolean existsByUserIdAndTargetUserId(Long userId, Long targetId);
    boolean existsByUserIdAndBlacklistTypeAndTargetUserId(Long userId, BlacklistType blacklistType, Long targetId);

    void deleteByUserIdAndBlacklistTypeAndTargetUserId(Long userId, BlacklistType blacklistType, Long targetId);
    void deleteByUserIdAndTargetUserId(Long userId, Long targetId);

    @EntityGraph(attributePaths = {
            "targetUser"
    })
    List<UserBlacklist> findAllByUserIdAndBlacklistType(Long userId, BlacklistType blacklistType);
}
