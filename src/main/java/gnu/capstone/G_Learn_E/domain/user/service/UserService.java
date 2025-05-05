package gnu.capstone.G_Learn_E.domain.user.service;

import gnu.capstone.G_Learn_E.domain.folder.entity.Folder;
import gnu.capstone.G_Learn_E.domain.folder.repository.FolderRepository;
import gnu.capstone.G_Learn_E.domain.public_folder.entity.College;
import gnu.capstone.G_Learn_E.domain.public_folder.entity.Department;
import gnu.capstone.G_Learn_E.domain.user.entity.User;
import gnu.capstone.G_Learn_E.domain.user.exception.UserInvalidException;
import gnu.capstone.G_Learn_E.domain.user.exception.UserNotFoundException;
import gnu.capstone.G_Learn_E.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final FolderRepository folderRepository;
    private final PasswordEncoder passwordEncoder;

    public User save(User user) {
        return userRepository.save(user);
    }
    public User save(String name, String nickname, String email, String password, College college, Department department) {
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
                .college(college)
                .department(department)
                .build();
        user = userRepository.save(user);

        Folder folder = Folder.builder()
                .name("기본 폴더")
                .user(user)
                .parent(null)
                .build();
        folderRepository.save(folder);
        return user;
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