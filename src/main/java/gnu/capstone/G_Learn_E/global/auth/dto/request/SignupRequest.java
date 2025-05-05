package gnu.capstone.G_Learn_E.global.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignupRequest(
        @NotBlank(message = "이름을 입력해주세요.")
        @Size(min = 2, max = 5, message = "이름은 2~5자 사이로 입력해주세요.")
        String name,

        @NotBlank(message = "닉네임을 입력해주세요.")
        @Size(min = 2, max = 20, message = "닉네임은 2~20자 사이로 입력해주세요.")
        String nickname,

        @NotBlank(message = "이메일을 입력해주세요.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String email,

        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=])[A-Za-z\\d!@#$%^&*()_+\\-=]{8,20}$",
                message = "비밀번호는 8~20자, 영문+숫자+특수문자를 포함해야 합니다."
        )
        String password,
        Long collegeId,
        Long departmentId
) {
}