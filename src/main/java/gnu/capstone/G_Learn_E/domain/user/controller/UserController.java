package gnu.capstone.G_Learn_E.domain.user.controller;

import gnu.capstone.G_Learn_E.domain.user.dto.response.NicknameUpdateResponse;
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
    @GetMapping
    public ApiResponse<UserInfoResponse> getInfo(@AuthenticationPrincipal User user) {
        UserInfoResponse response = UserInfoResponse.from(user);
        return new ApiResponse<>(HttpStatus.OK, "유저 정보 조회 성공", response);
    }

    @PostMapping("/exp/gain")
    public ApiResponse<?> gainExp(
            @AuthenticationPrincipal User user,
            @RequestParam Integer exp
    ) {
        userService.gainExp(user, exp);
        return new ApiResponse<>(HttpStatus.OK, "경험치가 증가했습니다.", null);
    }

    @PatchMapping("/nickname")
    public ApiResponse<NicknameUpdateResponse> updateNickname(
            @AuthenticationPrincipal User user,
            @RequestParam String nickname
    ) {
        userService.updateNickname(user, nickname);
        return new ApiResponse<>(HttpStatus.OK, "닉네임이 변경되었습니다.", null);
    }

}