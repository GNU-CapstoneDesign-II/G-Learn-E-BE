package gnu.capstone.G_Learn_E.global.auth.controller;

import gnu.capstone.G_Learn_E.domain.public_folder.entity.College;
import gnu.capstone.G_Learn_E.domain.public_folder.entity.Department;
import gnu.capstone.G_Learn_E.domain.public_folder.service.PublicFolderService;
import gnu.capstone.G_Learn_E.domain.user.entity.User;
import gnu.capstone.G_Learn_E.domain.user.service.UserService;
import gnu.capstone.G_Learn_E.global.auth.dto.request.EmailAuthCodeVerify;
import gnu.capstone.G_Learn_E.global.auth.dto.request.LoginRequest;
import gnu.capstone.G_Learn_E.global.auth.dto.request.PasswordChangeRequest;
import gnu.capstone.G_Learn_E.global.auth.dto.request.SignupRequest;
import gnu.capstone.G_Learn_E.global.auth.dto.response.AccessTokenResponse;
import gnu.capstone.G_Learn_E.global.auth.dto.response.EmailAuthToken;
import gnu.capstone.G_Learn_E.global.auth.dto.response.TokenResponse;
import gnu.capstone.G_Learn_E.global.auth.exception.AuthInvalidException;
import gnu.capstone.G_Learn_E.global.auth.service.AuthService;
import gnu.capstone.G_Learn_E.global.auth.util.EmailValidator;
import gnu.capstone.G_Learn_E.global.jwt.service.JwtService;
import gnu.capstone.G_Learn_E.global.mail.EmailSender;
import gnu.capstone.G_Learn_E.global.template.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@Tag(name = "인증 API")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final PublicFolderService publicFolderService;
    private final AuthService authService;
    private final EmailValidator emailValidator;
    private final EmailSender emailSender;
    private final JwtService jwtService;


    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인합니다.")
    @PostMapping("/login")
    public ApiResponse<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        String email = request.email();
        String password = request.password();

        User user = authService.login(email, password);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateAndStoreRefreshToken(user);

        TokenResponse tokenResponse = new TokenResponse(accessToken, refreshToken);
        return new ApiResponse<>(HttpStatus.OK, "로그인 성공", tokenResponse);
    }

    @Operation(summary = "회원가입", description = "이메일 인증 후 회원가입합니다.")
    @PostMapping("/signup")
    public ApiResponse<TokenResponse> signUp(HttpServletRequest request, @Valid @RequestBody SignupRequest dto) {
        // 회원가입
        String name = dto.name();
        String nickname = dto.nickname();
        String email = dto.email();
        String password = dto.password();
        Long collegeId = dto.collegeId();
        Long departmentId = dto.departmentId();

        College college = publicFolderService.getCollege(collegeId);
        Department department = publicFolderService.getDepartmentByCollegeId(collegeId, departmentId);

        // 토큰 이메일과 입력받은 이메일 검증
        String tokenEmail = (String) request.getAttribute("email");
        if(!email.equals(tokenEmail)) {
            // TODO : 예외 처리
            throw AuthInvalidException.emailAndTokenNotMatch();
        }

        User savedUser = userService.save(name, nickname, email, password, college, department);

        String emailAuthToken = jwtService.extractToken(request);
        jwtService.setBlacklistToken(emailAuthToken);

        String accessToken = jwtService.generateAccessToken(savedUser);
        String refreshToken = jwtService.generateAndStoreRefreshToken(savedUser);

        TokenResponse tokenResponse = new TokenResponse(accessToken, refreshToken);
        return new ApiResponse<>(HttpStatus.CREATED, "회원가입 성공", tokenResponse);
    }

    @Operation(summary = "이메일 인증 코드 발급", description = "이메일 인증 코드를 발급합니다.")
    @GetMapping("/email-code")
    public ApiResponse<?> getEmailAuthCode(@RequestParam("email") String email) {
        // TODO : 이메일 검증
        emailValidator.validate(email);
        // TODO : 이메일 인증 코드 발급
        String authCode = authService.issueEmailAuthCode(email);
        emailSender.sendAuthCode(email, authCode);

        return new ApiResponse<>(HttpStatus.NO_CONTENT, "이메일 인증 코드 발급 성공", null);
    }

    @Operation(summary = "이메일 인증 코드 검증", description = "이메일 인증 코드를 검증합니다.")
    @PostMapping("/email-code/verify")
    public ApiResponse<EmailAuthToken> verifyEmailAuthCode(@RequestBody EmailAuthCodeVerify request) {
        // TODO : 이메일 인증 코드 검증
        authService.verifyEmailAuthCode(request.email(), request.authCode());

        String emailAuthToken = jwtService.generateEmailAuthToken(request.email());
        log.info("이메일 인증 코드 검증 성공 [email: {}]", request.email());
        log.info("이메일 인증 토큰 발급 [email: {}, token: {}]", request.email(), emailAuthToken);

        String responseMsg = String.format("이메일 인증 코드 검증 성공. 유효 시간: %d분", jwtService.getEmailAuthTokenExpiration() / 1000 / 60);
        return new ApiResponse<>(HttpStatus.OK, responseMsg, new EmailAuthToken(emailAuthToken));
    }

    @Operation(summary = "엑세스 토큰 재발급", description = "엑세스 토큰을 재발급합니다.")
    @PatchMapping("/reissue")
    public ApiResponse<AccessTokenResponse> reissue(@AuthenticationPrincipal User user, HttpServletRequest request) {
        String refreshToken = jwtService.extractToken(request);
        String accessToken = jwtService.reissueAccessToken(user, refreshToken);

        AccessTokenResponse response = AccessTokenResponse.of(accessToken);
        return new ApiResponse<>(HttpStatus.OK, "엑세스 토큰 재발급 성공", response);
    }

    @Operation(summary = "로그아웃", description = "로그아웃합니다. 엑세스 토큰과 리프레시 토큰을 블랙리스트에 추가합니다.")
    @DeleteMapping("/logout")
    public ApiResponse<?> logout(@AuthenticationPrincipal User user, HttpServletRequest request) {
        String accessToken = jwtService.extractToken(request);
        jwtService.logout(user, accessToken);
        log.info("로그아웃 성공 [userId : {}]", user.getId());
        return new ApiResponse<>(HttpStatus.NO_CONTENT, "로그아웃 성공", null);
    }


    @PatchMapping("/password")
    @Operation(summary = "비밀번호 변경", description = "비밀번호를 변경합니다.")
    public ApiResponse<?> changePassword(
            @AuthenticationPrincipal User user,
            @RequestBody PasswordChangeRequest request
    ) {
        if(!request.newPassword().equals(request.oldPassword())) {
            throw new AuthInvalidException("변경할 비밀번호가 일치하지 않습니다.");
        }

        authService.updatePassword(user, request.oldPassword(), request.newPassword());
        return new ApiResponse<>(HttpStatus.OK, "비밀번호 변경 성공", null);
    }

    @PatchMapping("/password/forgot")
    @Operation(summary = "비밀번호 찾기", description = "비밀번호를 찾습니다.")
    public ApiResponse<?> findPassword(
            @RequestBody PasswordChangeRequest request
    ) {
        authService.updatePassword(request.email(), request.newPassword());
        return new ApiResponse<>(HttpStatus.OK, "비밀번호 변경 성공", null);
    }
}

