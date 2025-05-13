package gnu.capstone.G_Learn_E.domain.workbook.entity;


import gnu.capstone.G_Learn_E.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "downloaded_workbook_map",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_download_user_workbook",
                columnNames = {"user_id", "workbook_id"}
        )
)
@Getter
@NoArgsConstructor
public class DownloadedWorkbookMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workbook_id", nullable = false)
    private Workbook workbook;

    @Column(name = "downloaded_at", nullable = false, updatable = false)
    private LocalDateTime downloadedAt;

    @PrePersist
    private void prePersist() {
        this.downloadedAt = LocalDateTime.now();
    }

    @Builder
    public DownloadedWorkbookMap(User user, Workbook workbook) {
        this.user = user;
        this.workbook = workbook;
    }
}