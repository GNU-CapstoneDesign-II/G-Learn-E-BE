package gnu.capstone.G_Learn_E.domain.user.service;

import gnu.capstone.G_Learn_E.domain.user.entity.User;
import gnu.capstone.G_Learn_E.domain.user.entity.UserBlacklist;
import gnu.capstone.G_Learn_E.domain.user.enums.BlacklistType;
import gnu.capstone.G_Learn_E.domain.user.repository.UserBlacklistRepository;
import gnu.capstone.G_Learn_E.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserBlacklistService {

    private final UserRepository userRepository;
    private final UserBlacklistRepository userBlacklistRepository;


    @Transactional
    public UserBlacklist addBlacklist(User user, Long targetId, String blacklistType) {
        BlacklistType type = null;
        if (blacklistType.equals("BLOCK")) {
            type = BlacklistType.BLOCK;
        } else if (blacklistType.equals("HIDE")) {
            type = BlacklistType.HIDE;
        } else {
            // 한글로 예외처리
            throw new IllegalArgumentException("잘못된 블랙리스트 타입입니다.");
        }
        if (userBlacklistRepository.existsByUserIdAndTargetUserId(user.getId(), targetId)) {
            throw new IllegalArgumentException("이미 블랙리스트에 추가된 사용자입니다.");
        }

        User targetUser = userRepository.findById(targetId).orElseThrow(() -> new IllegalArgumentException("대상 사용자를 찾을 수 없습니다."));

        UserBlacklist userBlacklist = UserBlacklist.builder()
                .user(user)
                .targetUser(targetUser)
                .blacklistType(type)
                .build();

        return userBlacklistRepository.save(userBlacklist);
    }

    @Transactional
    public void removeBlacklist(User user, Long targetId) {
        if (!userBlacklistRepository.existsByUserIdAndTargetUserId(user.getId(), targetId)) {
            throw new IllegalArgumentException("블랙리스트에 추가된 사용자가 아닙니다.");
        }
        if(!userRepository.existsById(targetId)) {
            throw new IllegalArgumentException("대상 사용자가 존재하지 않습니다.");
        }

        userBlacklistRepository.deleteByUserIdAndTargetUserId(user.getId(), targetId);
    }

    @Transactional(readOnly = true)
    public List<UserBlacklist> getBlacklists(User user, String blacklistType) {
        BlacklistType type = null;
        if (blacklistType.equals("BLOCK")) {
            type = BlacklistType.BLOCK;
        } else if (blacklistType.equals("HIDE")) {
            type = BlacklistType.HIDE;
        } else {
            // 한글로 예외처리
            throw new IllegalArgumentException("잘못된 블랙리스트 타입입니다.");
        }

        return userBlacklistRepository.findAllByUserIdAndBlacklistType(user.getId(), type);
    }
}
