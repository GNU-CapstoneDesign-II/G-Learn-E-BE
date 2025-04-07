package gnu.capstone.G_Learn_E.global.auth.controller;

import gnu.capstone.G_Learn_E.domain.user.entity.User;
import gnu.capstone.G_Learn_E.domain.user.service.UserService;
import gnu.capstone.G_Learn_E.global.auth.dto.request.EmailAuthCodeVerify;
import gnu.capstone.G_Learn_E.global.auth.dto.request.LoginRequest;
import gnu.capstone.G_Learn_E.global.auth.dto.request.SignupRequest;
import gnu.capstone.G_Learn_E.global.auth.dto.response.EmailAuthToken;
import gnu.capstone.G_Learn_E.global.auth.dto.response.TokenResponse;
import gnu.capstone.G_Learn_E.global.auth.exception.AuthInvalidException;
import gnu.capstone.G_Learn_E.global.auth.service.AuthService;
import gnu.capstone.G_Learn_E.global.auth.util.EmailValidator;
import gnu.capstone.G_Learn_E.global.jwt.JwtUtils;
import gnu.capstone.G_Learn_E.global.mail.EmailSender;
import gnu.capstone.G_Learn_E.global.template.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;
    private final EmailValidator emailValidator;
    private final EmailSender emailSender;
    private final JwtUtils jwtUtils;


    @PostMapping("/login")
    public ApiResponse<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        String email = request.email();
        String password = request.password();

        User user = authService.login(email, password);

        // TODO : 리프레시 토큰 저장 로직 추가
        String accessToken = jwtUtils.generateAccessToken(user);
        String refreshToken = jwtUtils.generateRefreshToken(user);

        log.info("로그인 성공 [accessToken : {}, refreshToken : {}]", accessToken, refreshToken);
        log.info("[email: {}]", email);

        TokenResponse tokenResponse = new TokenResponse(accessToken, refreshToken);
        return new ApiResponse<>(HttpStatus.OK, "로그인 성공", tokenResponse);
    }

    @PostMapping("/signup")
    public ApiResponse<TokenResponse> signUp(HttpServletRequest request, @Valid @RequestBody SignupRequest dto) {
        // 회원가입
        String name = dto.name();
        String nickname = dto.nickname();
        String email = dto.email();
        String password = dto.password();

        // 토큰 이메일과 입력받은 이메일 검증
        String tokenEmail = (String) request.getAttribute("email");
        if(!email.equals(tokenEmail)) {
            // TODO : 예외 처리
            throw AuthInvalidException.emailAndTokenNotMatch();
        }

        User savedUser = userService.save(name, nickname, email, password);

        // TODO : 리프레시 토큰 저장 로직 추가
        String accessToken = jwtUtils.generateAccessToken(savedUser);
        String refreshToken = jwtUtils.generateRefreshToken(savedUser);

        log.info("회원가입 성공 [accessToken : {}, refreshToken : {}]", accessToken, refreshToken);
        log.info("[name: {}, nickname: {}, email: {}]", name, nickname, email);

        TokenResponse tokenResponse = new TokenResponse(accessToken, refreshToken);
        return new ApiResponse<>(HttpStatus.CREATED, "회원가입 성공", tokenResponse);
    }

    @GetMapping("/email-code")
    public ApiResponse<?> getEmailAuthCode(@RequestParam("email") String email) {
        // TODO : 이메일 검증
        emailValidator.validate(email);
        // TODO : 이메일 인증 코드 발급
        String authCode = authService.issueEmailAuthCode(email);
        emailSender.sendAuthCode(email, authCode);

        return new ApiResponse<>(HttpStatus.NO_CONTENT, "이메일 인증 코드 발급 성공", null);
    }

    @PostMapping("/email-code/verify")
    public ApiResponse<EmailAuthToken> verifyEmailAuthCode(@RequestBody EmailAuthCodeVerify request) {
        // TODO : 이메일 인증 코드 검증
        authService.verifyEmailAuthCode(request.email(), request.authCode());

        String emailAuthToken = jwtUtils.generateEmailAuthToken(request.email());
        log.info("이메일 인증 코드 검증 성공 [email: {}]", request.email());
        log.info("이메일 인증 토큰 발급 [email: {}, token: {}]", request.email(), emailAuthToken);

        String responseMsg = String.format("이메일 인증 코드 검증 성공. 유효 시간: %d분", jwtUtils.getEmailAuthTokenExpiration() / 1000 / 60);
        return new ApiResponse<>(HttpStatus.OK, responseMsg, new EmailAuthToken(emailAuthToken));
    }
}
