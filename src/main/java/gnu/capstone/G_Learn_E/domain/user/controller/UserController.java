package gnu.capstone.G_Learn_E.domain.user.controller;

import gnu.capstone.G_Learn_E.domain.public_folder.dto.response.CollegeResponse;
import gnu.capstone.G_Learn_E.domain.public_folder.entity.College;
import gnu.capstone.G_Learn_E.domain.user.dto.request.NicknameUpdateRequest;
import gnu.capstone.G_Learn_E.domain.user.dto.response.NicknameUpdateResponse;
import gnu.capstone.G_Learn_E.domain.user.dto.response.UserExpResponse;
import gnu.capstone.G_Learn_E.domain.user.dto.request.GainExpRequest;
import gnu.capstone.G_Learn_E.domain.user.dto.response.UserInfoResponse;
import gnu.capstone.G_Learn_E.domain.user.entity.User;
import gnu.capstone.G_Learn_E.domain.user.service.UserService;
import gnu.capstone.G_Learn_E.global.template.RestTemplate;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    @GetMapping("/info/{userId}") // 경로 매핑 수정
    public RestTemplate<List<UserInfoResponse>> getInfo(@PathVariable("userId") Long userId) {
        List<User> users = userService.findAll(userId);
        List<UserInfoResponse> responses = users.stream()
                .map(UserInfoResponse::from)
                .toList();

        return new RestTemplate<>(HttpStatus.OK, "유저 정보 조회 성공", responses);
    }

    @PostMapping("/exp/gain")
    public RestTemplate<UserExpResponse> gainExp(
            @RequestParam Long userId,
            @RequestParam Integer exp
    ) {
        UserExpResponse response = userService.gainExp(userId, exp);
        return new RestTemplate<>(HttpStatus.OK, "경험치가 증가했습니다.", response);
    }
    @PatchMapping("/nickname")
    public RestTemplate<NicknameUpdateResponse> updateNickname(
            @RequestParam Long userId,    // ✅ Query Parameter에서 userId 가져오기
            @RequestParam String nickname // ✅ Query Parameter에서 nickname 가져오기
    ) {
        NicknameUpdateResponse response = userService.updateNickname(userId, nickname);
        return new RestTemplate<>(HttpStatus.OK, "닉네임이 변경되었습니다.", response);
    }


}
    // TODO : 유저 컨트롤러 구현
