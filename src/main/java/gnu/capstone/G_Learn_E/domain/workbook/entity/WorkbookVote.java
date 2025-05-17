package gnu.capstone.G_Learn_E.domain.workbook.entity;

import gnu.capstone.G_Learn_E.domain.user.entity.User;
import gnu.capstone.G_Learn_E.domain.workbook.enums.WorkbookVoteType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "workbook_vote",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "workbook_id"}))
@NoArgsConstructor
public class WorkbookVote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workbook_id", nullable = false)
    private Workbook workbook;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "vote_type", nullable = false)
    private WorkbookVoteType voteType;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder
    public WorkbookVote(User user, Workbook workbook, WorkbookVoteType voteType) {
        this.user = user;
        this.workbook = workbook;
        this.voteType = voteType;
    }
}
