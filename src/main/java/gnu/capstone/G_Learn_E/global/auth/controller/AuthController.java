package gnu.capstone.G_Learn_E.global.auth.controller;

import gnu.capstone.G_Learn_E.global.auth.dto.response.EmailAuthCode;
import gnu.capstone.G_Learn_E.global.auth.service.AuthService;
import gnu.capstone.G_Learn_E.global.auth.util.EmailValidator;
import gnu.capstone.G_Learn_E.global.jwt.JwtUtils;
import gnu.capstone.G_Learn_E.global.mail.EmailSender;
import gnu.capstone.G_Learn_E.global.template.RestTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final EmailValidator emailValidator;
    private final EmailSender emailSender;
    private final JwtUtils jwtUtils;

    @GetMapping("/email-code")
    public RestTemplate<?> getEmailAuthCode(@RequestParam String email) {
        // TODO : 이메일 검증
        emailValidator.validate(email);
        // TODO : 이메일 인증 코드 발급
        String authCode = authService.issueEmailAuthCode(email);
        emailSender.sendAuthCode(email, authCode);

        return new RestTemplate<>(HttpStatus.NO_CONTENT, "이메일 인증 코드 발급 성공", null);
    }
}
