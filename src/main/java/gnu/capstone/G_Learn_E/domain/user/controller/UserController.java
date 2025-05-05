package gnu.capstone.G_Learn_E.domain.user.controller;

import gnu.capstone.G_Learn_E.domain.public_folder.entity.College;
import gnu.capstone.G_Learn_E.domain.public_folder.entity.Department;
import gnu.capstone.G_Learn_E.domain.public_folder.service.PublicFolderService;
import gnu.capstone.G_Learn_E.domain.user.dto.request.AffiliationUpdateRequest;
import gnu.capstone.G_Learn_E.domain.user.dto.request.GainExpRequest;
import gnu.capstone.G_Learn_E.domain.user.dto.request.NicknameUpdateRequest;
import gnu.capstone.G_Learn_E.domain.user.dto.response.UserInfoResponse;
import gnu.capstone.G_Learn_E.domain.user.entity.User;
import gnu.capstone.G_Learn_E.domain.user.service.UserService;
import gnu.capstone.G_Learn_E.global.template.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/user")
@Tag(name = "유저 API")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
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
}