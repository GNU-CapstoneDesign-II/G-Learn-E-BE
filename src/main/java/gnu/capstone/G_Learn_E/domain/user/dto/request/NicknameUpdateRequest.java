package gnu.capstone.G_Learn_E.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;


public record NicknameUpdateRequest(
        String newNickname
){
}