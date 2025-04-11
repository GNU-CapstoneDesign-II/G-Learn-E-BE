package gnu.capstone.G_Learn_E.domain.user.controller;

import gnu.capstone.G_Learn_E.domain.user.dto.response.NicknameUpdateResponse;
import gnu.capstone.G_Learn_E.domain.user.dto.response.UserExpResponse;
import gnu.capstone.G_Learn_E.domain.user.dto.response.UserInfoResponse;
import gnu.capstone.G_Learn_E.domain.user.entity.User;
import gnu.capstone.G_Learn_E.domain.user.service.UserService;
import gnu.capstone.G_Learn_E.global.template.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 🔹 액세스 토큰 기반 유저 정보 조회
    @GetMapping("/info")
    public ApiResponse<UserInfoResponse> getMyInfo(@AuthenticationPrincipal User user) {
        UserInfoResponse response = UserInfoResponse.from(user);
        return new ApiResponse<>(HttpStatus.OK, "유저 정보 조회 성공", response);
    }

    // 🔹 액세스 토큰 기반 경험치 증가
    @PostMapping("/exp/gain")
    public ApiResponse<UserExpResponse> gainExp(
            @AuthenticationPrincipal User user,
            @RequestParam Integer exp
    ) {
        UserExpResponse response = userService.gainExp(user.getId(), exp);
        return new ApiResponse<>(HttpStatus.OK, "경험치가 증가했습니다.", response);
    }

    // 🔹 액세스 토큰 기반 닉네임 변경
    @PatchMapping("/nickname")
    public ApiResponse<NicknameUpdateResponse> updateNickname(
            @AuthenticationPrincipal User user,
            @RequestParam String nickname
    ) {
        NicknameUpdateResponse response = userService.updateNickname(user.getId(), nickname);
        return new ApiResponse<>(HttpStatus.OK, "닉네임이 변경되었습니다.", response);
    }
}
