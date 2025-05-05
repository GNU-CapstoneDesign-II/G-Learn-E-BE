package gnu.capstone.G_Learn_E.domain.solve_log.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Embeddable
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SolvedWorkbookId implements Serializable {
    private Long userId;
    private Long workbookId;
}
