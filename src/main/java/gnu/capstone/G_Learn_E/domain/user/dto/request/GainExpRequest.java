package gnu.capstone.G_Learn_E.domain.user.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class GainExpRequest {
    @NotNull(message = "userId는 필수입니다.")
    private Long userId;

    @NotNull(message = "exp는 필수입니다.")
    private Integer exp;
}
