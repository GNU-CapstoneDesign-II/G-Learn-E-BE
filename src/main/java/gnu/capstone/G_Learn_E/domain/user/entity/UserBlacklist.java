package gnu.capstone.G_Learn_E.domain.user.entity;

import gnu.capstone.G_Learn_E.domain.user.enums.BlacklistType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "user_blacklist",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "target_user_id"}))
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class UserBlacklist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id", nullable = false)
    private User targetUser;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BlacklistType blacklistType;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Builder
    public UserBlacklist(User user, User targetUser, BlacklistType blacklistType) {
        this.user = user;
        this.targetUser = targetUser;
        this.blacklistType = blacklistType;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }


}
