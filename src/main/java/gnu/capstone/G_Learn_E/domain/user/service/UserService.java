package gnu.capstone.G_Learn_E.domain.user.service;

import gnu.capstone.G_Learn_E.domain.folder.entity.Folder;
import gnu.capstone.G_Learn_E.domain.folder.repository.FolderRepository;
import gnu.capstone.G_Learn_E.domain.public_folder.entity.College;
import gnu.capstone.G_Learn_E.domain.public_folder.entity.Department;
import gnu.capstone.G_Learn_E.domain.user.dto.CollegeRanking;
import gnu.capstone.G_Learn_E.domain.user.dto.DepartmentRanking;
import gnu.capstone.G_Learn_E.domain.user.dto.response.CollegeRankingPageResponse;
import gnu.capstone.G_Learn_E.domain.user.dto.response.DepartmentRankingPageResponse;
import gnu.capstone.G_Learn_E.global.common.dto.serviceToController.UserPaginationResult;
import gnu.capstone.G_Learn_E.domain.user.entity.User;
import gnu.capstone.G_Learn_E.domain.user.exception.UserInvalidException;
import gnu.capstone.G_Learn_E.domain.user.exception.UserNotFoundException;
import gnu.capstone.G_Learn_E.domain.user.repository.UserRepository;
import gnu.capstone.G_Learn_E.global.common.dto.response.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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

    @Transactional
    public User updateUserInfo(User user, String name, String nickname, College college, Department department) {
        if(!user.getNickname().equals(nickname) && userRepository.existsByNickname(nickname)) {
            // 닉네임 중복 체크
            throw UserInvalidException.existsNickname();
        }
        user.setName(name);
        user.setNickname(nickname);

        if(!college.isCollege()){
            throw new RuntimeException("유효한 단과대학이 아닙니다.");
        }
        user.setCollege(college);
        user.setDepartment(department);
        return userRepository.save(user);
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

    @Transactional
    public User updateAffiliation(User user, College college, Department department) {
        if(!college.isCollege()){
            throw new RuntimeException("유효한 단과대학이 아닙니다.");
        }
        user.setCollege(college);
        user.setDepartment(department);
        return userRepository.save(user);
    }

    public void plusCreateWorkbookCount(User user) {
        user.plusCreateWorkbookCount();
        userRepository.save(user);
    }

    /**
     * 특정 유저의 랭킹 계산 (1위 = 1)
     */
    public long getUserRank(User user) {
        long higherCount = userRepository
                .countHigherRanked(user.getLevel(), user.getExp());
        return higherCount + 1;
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

    // ranking --------------------------------------------------------------------------------------
    public UserPaginationResult getUserRankList(int page, int size, String sort) {
        Sort sortObj;
        if ("level".equalsIgnoreCase(sort)) {
            // 레벨 내림차순, 경험치 내림차순
            sortObj = Sort.by(
                    Sort.Order.desc("level"),
                    Sort.Order.desc("exp")
            );
        } else if ("solveWorkbook".equalsIgnoreCase(sort)) {
            // 푼 워크북 수 내림차순
            sortObj = Sort.by(
                    Sort.Order.desc("solvedWorkbookCount")
            );
        } else if ("createWorkbook".equalsIgnoreCase(sort)) {
            // 생성한 워크북 수 내림차순
            sortObj = Sort.by(
                    Sort.Order.desc("createWorkbookCount")
            );
        } else {
            throw new RuntimeException("정렬 기준이 잘못되었습니다.");
        }

        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<User> userPage = userRepository.findAll(pageable);
        PageInfo pageInfo = PageInfo.of(
                userPage.getTotalElements(),
                userPage.getTotalPages(),
                page,
                userPage.hasNext(),
                userPage.hasPrevious()
        );
        return UserPaginationResult.from(
                pageInfo,
                userPage.getContent()
        );
    }

    public UserPaginationResult getUserRankListInDepartment(
            int page, int size, String sort, Long departmentId
    ) {
        // 1) Sort 객체 생성
        Sort sortObj;
        if ("level".equalsIgnoreCase(sort)) {
            sortObj = Sort.by(
                    Sort.Order.desc("level"),
                    Sort.Order.desc("exp")
            );
        } else if ("solveWorkbook".equalsIgnoreCase(sort)) {
            sortObj = Sort.by(
                    Sort.Order.desc("solvedWorkbookCount")
            );
        } else if ("createWorkbook".equalsIgnoreCase(sort)) {
            sortObj = Sort.by(
                    Sort.Order.desc("createWorkbookCount")
            );
        } else {
            throw new IllegalArgumentException("정렬 기준이 잘못되었습니다.");
        }

        // 2) 페이징·정렬 정보
        Pageable pageable = PageRequest.of(page, size, sortObj);

        // 3) 학과별 분기 호출
        Page<User> userPage;
        if ("level".equalsIgnoreCase(sort)) {
            userPage = userRepository
                    .findByDepartmentIdOrderByLevelDescExpDesc(departmentId, pageable);
        } else if ("solveWorkbook".equalsIgnoreCase(sort)) {
            userPage = userRepository
                    .findByDepartmentIdOrderBySolvedWorkbookCountDesc(departmentId, pageable);
        } else { // createWorkbook
            userPage = userRepository
                    .findByDepartmentIdOrderByCreateWorkbookCountDesc(departmentId, pageable);
        }

        // 4) 페이지 정보
        PageInfo pageInfo = PageInfo.of(
                userPage.getTotalElements(),
                userPage.getTotalPages(),
                page,
                userPage.hasNext(),
                userPage.hasPrevious()
        );

        return UserPaginationResult.from(
                pageInfo,
                userPage.getContent()
        );
    }

    public UserPaginationResult getUserRankListInCollege(
            int page, int size, String sort, Long collegeId
    ) {
        // 1) Sort 객체 생성
        Sort sortObj;
        if ("level".equalsIgnoreCase(sort)) {
            sortObj = Sort.by(
                    Sort.Order.desc("level"),
                    Sort.Order.desc("exp")
            );
        } else if ("solveWorkbook".equalsIgnoreCase(sort)) {
            sortObj = Sort.by(
                    Sort.Order.desc("solvedWorkbookCount")
            );
        } else if ("createWorkbook".equalsIgnoreCase(sort)) {
            sortObj = Sort.by(
                    Sort.Order.desc("createWorkbookCount")
            );
        } else {
            throw new IllegalArgumentException("정렬 기준이 잘못되었습니다.");
        }

        // 2) 페이징·정렬 정보
        Pageable pageable = PageRequest.of(page, size, sortObj);

        // 3) 학과별 분기 호출
        Page<User> userPage;
        if ("level".equalsIgnoreCase(sort)) {
            userPage = userRepository
                    .findByCollegeIdOrderByLevelDescExpDesc(collegeId, pageable);
        } else if ("solveWorkbook".equalsIgnoreCase(sort)) {
            userPage = userRepository
                    .findByCollegeIdOrderBySolvedWorkbookCountDesc(collegeId, pageable);
        } else { // createWorkbook
            userPage = userRepository
                    .findByCollegeIdOrderByCreateWorkbookCountDesc(collegeId, pageable);
        }

        // 4) 페이지 정보
        PageInfo pageInfo = PageInfo.of(
                userPage.getTotalElements(),
                userPage.getTotalPages(),
                page,
                userPage.hasNext(),
                userPage.hasPrevious()
        );

        return UserPaginationResult.from(
                pageInfo,
                userPage.getContent()
        );
    }

    public DepartmentRankingPageResponse getDepartmentRankList(
            int page,
            int size,
            String sort
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DepartmentRanking> rows;

        if ("level".equalsIgnoreCase(sort)) {
            rows = userRepository.findDepartmentRankingsByLevel(pageable);
        } else if ("solveWorkbook".equalsIgnoreCase(sort)) {
            rows = userRepository.findDepartmentRankingsByTotalSolved(pageable);
        } else if ("createWorkbook".equalsIgnoreCase(sort)) {
            rows = userRepository.findDepartmentRankingsByTotalCreated(pageable);
        } else {
            throw new RuntimeException("정렬 기준이 잘못되었습니다.");
        }

        List<DepartmentRanking> content = rows.getContent();

        List<DepartmentRankingPageResponse.DepartmentRankingResponse> result = new ArrayList<>();
        long prevRank   = 0;
        int  sameCount  = 0;
        Double prevLvl  = null;
        Long   prevSolved = null;
        Long   prevCreated= null;

        for (int i = 0; i < content.size(); i++) {
            DepartmentRanking r = content.get(i);
            long rank;
            if (i == 0) {
                rank = 1;
                sameCount = 1;
            } else {
                boolean same = Objects.equals(r.level(), prevLvl)
                        && Objects.equals(r.solvedWorkbooks(), prevSolved)
                        && Objects.equals(r.createdWorkbooks(), prevCreated);
                if (same) {
                    rank = prevRank;
                    sameCount++;
                } else {
                    rank = prevRank + sameCount;
                    sameCount = 1;
                }
            }
            prevRank     = rank;
            prevLvl      = r.level();
            prevSolved   = r.solvedWorkbooks();
            prevCreated  = r.createdWorkbooks();

            result.add(DepartmentRankingPageResponse.DepartmentRankingResponse.of(
                    r.id(),
                    r.name(),
                    rank,
                    (long) r.level(),
                    r.solvedWorkbooks(),
                    r.createdWorkbooks()
            ));
        }

        PageInfo pageInfo = PageInfo.of(
                rows.getTotalElements(),
                rows.getTotalPages(),
                page,
                rows.hasNext(),
                rows.hasPrevious()
        );

        return DepartmentRankingPageResponse.from(pageInfo, result);
    }


    public CollegeRankingPageResponse getCollegeRankList(
            int page,
            int size,
            String sort
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CollegeRanking> rows;

        if ("level".equalsIgnoreCase(sort)) {
            rows = userRepository.findCollegeRankingsByLevel(pageable);
        } else if ("solveWorkbook".equalsIgnoreCase(sort)) {
            rows = userRepository.findCollegeRankingsByTotalSolved(pageable);
        } else if ("createWorkbook".equalsIgnoreCase(sort)) {
            rows = userRepository.findCollegeRankingsByTotalCreated(pageable);
        } else {
            throw new RuntimeException("정렬 기준이 잘못되었습니다.");
        }

        List<CollegeRanking> content = rows.getContent();

        List<CollegeRankingPageResponse.CollegeRankingResponse> result = new ArrayList<>();
        long prevRank   = 0;
        int  sameCount  = 0;
        Double prevLvl  = null;
        Long   prevSolved = null;
        Long   prevCreated= null;

        for (int i = 0; i < content.size(); i++) {
            CollegeRanking r = content.get(i);
            long rank;
            if (i == 0) {
                rank = 1;
                sameCount = 1;
            } else {
                boolean same = Objects.equals(r.level(), prevLvl)
                        && Objects.equals(r.solvedWorkbooks(), prevSolved)
                        && Objects.equals(r.createdWorkbooks(), prevCreated);
                if (same) {
                    rank = prevRank;
                    sameCount++;
                } else {
                    rank = prevRank + sameCount;
                    sameCount = 1;
                }
            }
            prevRank     = rank;
            prevLvl      = r.level();
            prevSolved   = r.solvedWorkbooks();
            prevCreated  = r.createdWorkbooks();

            result.add(CollegeRankingPageResponse.CollegeRankingResponse.of(
                    r.id(),
                    r.name(),
                    rank,
                    (long) r.level(),
                    r.solvedWorkbooks(),
                    r.createdWorkbooks()
            ));
        }

        PageInfo pageInfo = PageInfo.of(
                rows.getTotalElements(),
                rows.getTotalPages(),
                page,
                rows.hasNext(),
                rows.hasPrevious()
        );

        return CollegeRankingPageResponse.from(pageInfo, result);
    }
}