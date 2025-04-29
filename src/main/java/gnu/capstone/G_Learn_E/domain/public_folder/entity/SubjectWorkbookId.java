package gnu.capstone.G_Learn_E.domain.public_folder.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SubjectWorkbookId implements Serializable {
    private Long subjectId;
    private Long workbookId;
}
