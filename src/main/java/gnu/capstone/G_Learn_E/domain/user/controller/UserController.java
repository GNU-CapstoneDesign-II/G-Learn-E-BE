package gnu.capstone.G_Learn_E.domain.user.controller;

import gnu.capstone.G_Learn_E.domain.public_folder.entity.College;
import gnu.capstone.G_Learn_E.domain.public_folder.entity.Department;
import gnu.capstone.G_Learn_E.domain.public_folder.service.PublicFolderService;
import gnu.capstone.G_Learn_E.domain.solve_log.service.SolveLogService;
import gnu.capstone.G_Learn_E.domain.user.dto.request.*;
import gnu.capstone.G_Learn_E.domain.user.dto.response.*;
import gnu.capstone.G_Learn_E.domain.user.enums.ActivityType;
import gnu.capstone.G_Learn_E.domain.user.service.UserActivityLogService;
import gnu.capstone.G_Learn_E.global.common.dto.serviceToController.UserPaginationResult;
import gnu.capstone.G_Learn_E.domain.user.entity.User;
import gnu.capstone.G_Learn_E.domain.user.entity.UserBlacklist;
import gnu.capstone.G_Learn_E.domain.user.service.UserBlacklistService;
import gnu.capstone.G_Learn_E.domain.user.service.UserService;
import gnu.capstone.G_Learn_E.global.common.dto.response.PageInfo;
import gnu.capstone.G_Learn_E.global.template.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/user")
@Tag(name = "유저 API")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserBlacklistService userBlacklistService;
    private final UserActivityLogService userActivityLogService;
    private final SolveLogService solveLogService;
    private final PublicFolderService publicFolderService;

    @Operation(summary = "유저 정보 조회", description = "유저 정보를 조회합니다.")
    @GetMapping
    public ApiResponse<UserInfoResponse> getInfo(@AuthenticationPrincipal User user) {
        UserInfoResponse response = UserInfoResponse.from(user);
        return new ApiResponse<>(HttpStatus.OK, "유저 정보 조회 성공", response);
    }

    @Operation(summary = "유저 경험치 증가", description = "유저의 경험치를 증가시킵니다.")
    @PostMapping("/exp/gain")
    public ApiResponse<UserInfoResponse> gainExp(
            @AuthenticationPrincipal User user,
            @RequestBody GainExpRequest request
            ) {
        userService.gainExp(user, request.gainExp());
        UserInfoResponse response = UserInfoResponse.from(user);
        return new ApiResponse<>(HttpStatus.OK, "경험치가 증가했습니다.", response);
    }

    @Operation(summary = "유저 정보 수정", description = "유저 정보를 수정합니다.")
    @PatchMapping
    public ApiResponse<UserInfoResponse> updateInfo(
            @AuthenticationPrincipal User user,
            @RequestBody UserInfoUpdateRequest request
    ) {
        College college = publicFolderService.getCollege(request.collegeId());
        Department department = publicFolderService.getDepartmentByCollegeId(request.collegeId(), request.departmentId());

        user = userService.updateUserInfo(user, request.name(), request.nickname(), college, department);
        UserInfoResponse response = UserInfoResponse.from(user);
        return new ApiResponse<>(HttpStatus.OK, "유저 정보 수정 성공", response);
    }

    @Operation(summary = "유저 닉네임 변경", description = "유저의 닉네임을 변경합니다.")
    @PatchMapping("/nickname")
    public ApiResponse<UserInfoResponse> updateNickname(
            @AuthenticationPrincipal User user,
            @RequestBody NicknameUpdateRequest request
            ) {
        userService.updateNickname(user, request.newNickname());
        UserInfoResponse response = UserInfoResponse.from(user);
        return new ApiResponse<>(HttpStatus.OK, "닉네임이 변경되었습니다.", response);
    }

    @Operation(summary = "유저 소속 변경", description = "유저의 소속을 변경합니다.")
    @PatchMapping("/affiliation")
    public ApiResponse<UserInfoResponse> updateAffiliation(
            @AuthenticationPrincipal User user,
            @RequestBody AffiliationUpdateRequest request
    ) {
        College college = publicFolderService.getCollege(request.collegeId());
        Department department = publicFolderService.getDepartmentByCollegeId(request.collegeId(), request.departmentId());

        user = userService.updateAffiliation(user, college, department);
        UserInfoResponse response = UserInfoResponse.from(user);
        return new ApiResponse<>(HttpStatus.OK, "소속이 변경되었습니다.", response);
    }

    @Operation(summary = "유저 풀이 통계 조회", description = "유저의 풀이 통계를 조회합니다. 문제 생성 개수, 풀이 개수")
    @GetMapping("/solving-statistics")
    public ApiResponse<?> getSolvingStatistics(
            @AuthenticationPrincipal User user
    ) {
        solveLogService.updateSolvedWorkbookCountByUser(user);
        long solvedWorkbookCount = user.getSolvedWorkbookCount();
        long createWorkbookCount = user.getCreateWorkbookCount();
        long uploadedWorkbookCount = publicFolderService.uploadedWorkbookCountByUser(user);
        user.updateSolvedWorkbookCount(solvedWorkbookCount);
        long userRank = userService.getUserRank(user);

        userService.save(user);

        UserStatisticsResponse response = UserStatisticsResponse.of(userRank, createWorkbookCount, solvedWorkbookCount, uploadedWorkbookCount);
        return new ApiResponse<>(HttpStatus.OK, "유저 정보 조회 성공", response);
    }

    @Operation(summary = "유저 랭킹 조회", description = "유저 랭킹을 조회합니다. 정렬 기준 : level, solvedWorkbookCount, createWorkbookCount")
    @GetMapping("/ranking/user")
    public ApiResponse<?> getRanking(
            @RequestParam(name = "page", defaultValue = "0")
            int page,
            @RequestParam(name = "size", defaultValue = "10")
            int size,
            @RequestParam(name = "sort", defaultValue = "level")
            String sort
    ) {
        UserPaginationResult userPaginationResult = userService.getUserRankList(page, size, sort);
        List<User> rankList = userPaginationResult.userList();
        PageInfo pageInfo = userPaginationResult.pageInfo();

        UserRankingPageResponse response = UserRankingPageResponse.from(
                pageInfo,
                rankList,
                userService.getUserRank(rankList.getFirst())
        );

        return new ApiResponse<>(HttpStatus.OK, "유저 랭킹 조회 성공", response);
    }

    @Operation(summary = "학과 랭킹 조회", description = "학과 랭킹을 조회합니다. 정렬 기준 : level, solvedWorkbookCount, createWorkbookCount")
    @GetMapping("/ranking/department")
    public ApiResponse<?> getDepartmentRanking(
            @RequestParam(name = "page", defaultValue = "0")
            int page,
            @RequestParam(name = "size", defaultValue = "10")
            int size,
            @RequestParam(name = "sort", defaultValue = "level")
            String sort
    ) {
        DepartmentRankingPageResponse response = userService.getDepartmentRankList(page, size, sort);
        return new ApiResponse<>(HttpStatus.OK, "학과 랭킹 조회 성공", response);
    }

    @Operation(summary = "학과에서 유저 랭킹 조회", description = "학과에서 유저 랭킹을 조회합니다. 정렬 기준 : level, solvedWorkbookCount, createWorkbookCount")
    @GetMapping("/ranking/department/{departmentId}")
    public ApiResponse<?> getDepartmentRanking(
            @RequestParam(name = "page", defaultValue = "0")
            int page,
            @RequestParam(name = "size", defaultValue = "10")
            int size,
            @RequestParam(name = "sort", defaultValue = "level")
            String sort,
            @PathVariable("departmentId") Long departmentId
    ) {
        UserPaginationResult userPaginationResultInDepartment = userService.getUserRankListInDepartment(page, size, sort, departmentId);
        List<User> rankList = userPaginationResultInDepartment.userList();
        PageInfo pageInfo = userPaginationResultInDepartment.pageInfo();

        UserRankingPageResponse response = UserRankingPageResponse.from(
                pageInfo,
                rankList,
                userService.getUserRank(rankList.getFirst())
        );
        return new ApiResponse<>(HttpStatus.OK, "유저 랭킹 조회 성공", response);
    }

    @Operation(summary = "단과대 랭킹 조회", description = "단과대 랭킹을 조회합니다. 정렬 기준 : level, solvedWorkbookCount, createWorkbookCount")
    @GetMapping("/ranking/college")
    public ApiResponse<?> getCollegeRanking(
            @RequestParam(name = "page", defaultValue = "0")
            int page,
            @RequestParam(name = "size", defaultValue = "10")
            int size,
            @RequestParam(name = "sort", defaultValue = "level")
            String sort
    ) {
        CollegeRankingPageResponse response = userService.getCollegeRankList(page, size, sort);
        return new ApiResponse<>(HttpStatus.OK, "단과대 랭킹 조회 성공", response);
    }

    @Operation(summary = "단과대에서 유저 랭킹 조회", description = "단과대에서 유저 랭킹을 조회합니다. 정렬 기준 : level, solvedWorkbookCount, createWorkbookCount")
    @GetMapping("/ranking/college/{collegeId}")
    public ApiResponse<?> getCollegeRanking(
            @RequestParam(name = "page", defaultValue = "0")
            int page,
            @RequestParam(name = "size", defaultValue = "10")
            int size,
            @RequestParam(name = "sort", defaultValue = "level")
            String sort,
            @PathVariable("collegeId") Long collegeId
    ) {
        UserPaginationResult userPaginationResultInCollege = userService.getUserRankListInCollege(page, size, sort, collegeId);
        List<User> rankList = userPaginationResultInCollege.userList();
        PageInfo pageInfo = userPaginationResultInCollege.pageInfo();

        UserRankingPageResponse response = UserRankingPageResponse.from(
                pageInfo,
                rankList,
                userService.getUserRank(rankList.getFirst())
        );
        return new ApiResponse<>(HttpStatus.OK, "유저 랭킹 조회 성공", response);
    }


    @Operation(summary = "블랙리스트 조회", description = "블랙리스트를 조회합니다. 타입 종류 : BLOCK, HIDE")
    @GetMapping("/blacklist")
    public ApiResponse<?> getBlacklist(
            @AuthenticationPrincipal User user,
            @RequestParam(name = "blacklistType", defaultValue = "BLOCK")
            String blacklistType
    ) {
        List<UserBlacklist> blackList = userBlacklistService.getBlacklists(user, blacklistType);
        List<User> blacklistUsers = blackList.stream()
                .map(UserBlacklist::getTargetUser)
                .toList();
        BlacklistResponse response = BlacklistResponse.from(blacklistUsers);
        return new ApiResponse<>(HttpStatus.OK, "블랙리스트 조회 성공", response);
    }

    @Operation(summary = "블랙리스트 추가", description = "블랙리스트에 추가합니다. 타입 종류 : BLOCK, HIDE")
    @PostMapping("/blacklist")
    public ApiResponse<?> addBlacklist(
            @AuthenticationPrincipal User user,
            @RequestBody BlacklistRequest request
            ) {
        userBlacklistService.addBlacklist(user, request.targetId(), request.blacklistType());
        return new ApiResponse<>(HttpStatus.OK, "블랙리스트 추가 성공", null);
    }

    @Operation(summary = "블랙리스트 삭제", description = "블랙리스트에서 삭제합니다. 타입 종류 : BLOCK, HIDE")
    @DeleteMapping("/blacklist")
    public ApiResponse<?> removeBlacklist(
            @AuthenticationPrincipal User user,
            @RequestBody BlacklistRequest request
    ) {
        userBlacklistService.removeBlacklist(user, request.targetId());
        return new ApiResponse<>(HttpStatus.OK, "블랙리스트 삭제 성공", null);
    }

    @Operation(summary = "유저 활동 로그 조회", description = "유저의 활동 로그를 조회합니다.")
    @GetMapping("/activity-log")
    public ApiResponse<?> getActivityLog(
            @AuthenticationPrincipal User user,
            @RequestParam(name="types") List<ActivityType> types,
            @RequestParam(name = "days", defaultValue = "30") int days
    ) {
        List<DailyActivityCountResponse> dailyCounts = userActivityLogService.getDailyCounts(user.getId(), types, days);
        Map<String, Object> response = Map.of("activityLog", dailyCounts);
        return new ApiResponse<>(HttpStatus.OK, "유저 활동 로그 조회 성공", response);
    }
}