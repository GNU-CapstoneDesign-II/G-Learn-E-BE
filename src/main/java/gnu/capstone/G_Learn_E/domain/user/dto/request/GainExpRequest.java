package gnu.capstone.G_Learn_E.domain.user.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

public record GainExpRequest(
        Integer gainExp
){

}