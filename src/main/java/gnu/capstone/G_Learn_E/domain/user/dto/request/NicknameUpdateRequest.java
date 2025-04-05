package gnu.capstone.G_Learn_E.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class NicknameUpdateRequest {
    @NotBlank(message = "닉네임은 필수 입력 값입니다.")
    private String nickname;
}
