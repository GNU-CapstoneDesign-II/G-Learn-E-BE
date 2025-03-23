package gnu.capstone.G_Learn_E.domain.solve_log.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SolvedWorkbookId implements Serializable {
    private Long userId;
    private Long workbookId;
}
