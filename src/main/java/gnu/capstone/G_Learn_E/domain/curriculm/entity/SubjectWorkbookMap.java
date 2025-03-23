package gnu.capstone.G_Learn_E.domain.curriculm.entity;

import gnu.capstone.G_Learn_E.domain.workbook.entity.Workbook;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class SubjectWorkbookMap {

    @EmbeddedId
    private SubjectWorkbookId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("subjectId")
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("workbookId")
    @JoinColumn(name = "workbook_id")
    private Workbook workbook;
}
