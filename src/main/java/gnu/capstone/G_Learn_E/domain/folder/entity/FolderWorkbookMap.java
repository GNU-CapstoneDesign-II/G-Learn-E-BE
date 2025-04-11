package gnu.capstone.G_Learn_E.domain.folder.entity;

import gnu.capstone.G_Learn_E.domain.workbook.entity.Workbook;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class FolderWorkbookMap {

    @EmbeddedId
    private FolderWorkbookId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("folderId")
    @JoinColumn(name = "folder_id")
    private Folder folder;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("workbookId")
    @JoinColumn(name = "workbook_id")
    private Workbook workbook;

    @Builder
    public FolderWorkbookMap(Folder folder, Workbook workbook) {
        this.folder = folder;
        this.workbook = workbook;
        this.id = FolderWorkbookId.builder()
                .folderId(folder.getId())
                .workbookId(workbook.getId())
                .build();
    }
}
