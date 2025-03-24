package gnu.capstone.G_Learn_E.domain.user.service;

import gnu.capstone.G_Learn_E.domain.user.entity.User;
import gnu.capstone.G_Learn_E.domain.user.exception.UserNotFoundException;
import gnu.capstone.G_Learn_E.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;


    public User findById(Long id) {
        // TODO: 예외처리 변경
        return userRepository.findById(id)
                .orElseThrow(UserNotFoundException::userNotFound);
    }
    public User findById(String id) {
        long lid;
        try {
            lid = Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("id는 숫자로 입력해주세요.");
        }
        // TODO: 예외처리 변경
        return userRepository.findById(lid)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));
    }
}
