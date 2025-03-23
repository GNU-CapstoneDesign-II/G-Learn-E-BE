package gnu.capstone.G_Learn_E.domain.curriculm.entity;

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
public class SubjectWorkbookId implements Serializable {
    private Long subjectId;
    private Long workbookId;
}
