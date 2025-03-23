package gnu.capstone.G_Learn_E.domain.folder.entity;

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
public class FolderWorkbookId implements Serializable {
    private Long subjectId;
    private Long workbookId;
}
