package gnu.capstone.G_Learn_E.domain.solve_log.entity;

import gnu.capstone.G_Learn_E.domain.solve_log.enums.SolvingStatus;
import gnu.capstone.G_Learn_E.domain.user.entity.User;
import gnu.capstone.G_Learn_E.domain.workbook.entity.Workbook;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class SolvedWorkbook {
    @EmbeddedId
    private SolvedWorkbookId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("workbookId")
    @JoinColumn(name = "workbook_id")
    private Workbook workbook;

    @OneToMany(mappedBy = "solvedWorkbook", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SolveLog> solveLogs;

    @Enumerated(EnumType.STRING)
    private SolvingStatus status; // 풀이 상태 (진행 중, 완료 등)

    @Builder
    public SolvedWorkbook(SolvedWorkbookId id, User user, Workbook workbook) {
        this.id = id;
        this.user = user;
        this.workbook = workbook;
        this.status = SolvingStatus.NOT_STARTED;
    }
}
