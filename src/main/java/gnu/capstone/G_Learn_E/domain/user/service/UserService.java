package gnu.capstone.G_Learn_E.domain.user.service;

import gnu.capstone.G_Learn_E.domain.user.dto.response.NicknameUpdateResponse;
import gnu.capstone.G_Learn_E.domain.user.dto.response.UserExpResponse;
import gnu.capstone.G_Learn_E.domain.user.dto.response.UserInfoResponse;
import gnu.capstone.G_Learn_E.domain.user.entity.User;
import gnu.capstone.G_Learn_E.domain.user.exception.UserInvalidException;
import gnu.capstone.G_Learn_E.domain.user.exception.UserNotFoundException;
import gnu.capstone.G_Learn_E.domain.user.repository.UserRepository;
import gnu.capstone.G_Learn_E.domain.workbook.entity.Workbook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User save(User user) {
        return userRepository.save(user);
    }
    public User save(String name, String nickname, String email, String password) {
        if(existsByEmail(email)) {
            throw UserInvalidException.existsEmail();
        }
        if(existsByNickname(nickname)) {
            throw UserInvalidException.existsNickname();
        }

        User user = User.builder()
                .name(name)
                .nickname(nickname)
                .email(email)
                .password(passwordEncoder.encode(password))
                .build();
        return userRepository.save(user);
    }


    // find --------------------------------------------------------------------------------------------
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(UserNotFoundException::userNotFound);
    }
    public User findById(String id) {
        long lid;
        try {
            lid = Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw UserInvalidException.requestInvalid();
        }
        return userRepository.findById(lid)
                .orElseThrow(UserNotFoundException::userNotFound);
    }


    // exists ------------------------------------------------------------------------------------------
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    public boolean existsByNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    public List<User> findAll(Long userId) {
        return userRepository.findAllById(Collections.singleton(userId));
    }

    @Transactional
    public void gainExp(User user, Integer exp) {
        user.gainExp(exp);
        userRepository.save(user);
    }

    @Transactional
    public void updateNickname(User user, String newNickname) {
        if(existsByNickname(newNickname)) {
            // 닉네임 중복 체크
            throw UserInvalidException.existsNickname();
        }
        user.setNickname(newNickname);
        userRepository.save(user);
    }
}


