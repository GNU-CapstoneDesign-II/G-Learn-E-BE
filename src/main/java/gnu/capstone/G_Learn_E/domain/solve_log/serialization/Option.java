package gnu.capstone.G_Learn_E.domain.problem.serialization;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Option {
    private Short number; // 객관식 번호
    public String content; // 객관식 내용
}
