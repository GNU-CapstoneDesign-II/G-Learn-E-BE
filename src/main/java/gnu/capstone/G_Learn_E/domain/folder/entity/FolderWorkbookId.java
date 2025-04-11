package gnu.capstone.G_Learn_E.domain.folder.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Getter
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class FolderWorkbookId implements Serializable {
    private Long folderId;
    private Long workbookId;
}
