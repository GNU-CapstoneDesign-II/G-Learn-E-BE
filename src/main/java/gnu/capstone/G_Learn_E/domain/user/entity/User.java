package gnu.capstone.G_Learn_E.domain.user.entity;

import gnu.capstone.G_Learn_E.domain.folder.entity.Folder;
import gnu.capstone.G_Learn_E.domain.notification.entity.Notification;
import gnu.capstone.G_Learn_E.domain.public_folder.entity.College;
import gnu.capstone.G_Learn_E.domain.public_folder.entity.Department;
import gnu.capstone.G_Learn_E.domain.solve_log.entity.SolvedWorkbook;
import gnu.capstone.G_Learn_E.global.common.enums.UserStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Setter
    @Column(unique = true, nullable = false)
    private String nickname;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private Integer profileImage;

    private Short level;

    private Integer exp;

    private UserStatus status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "college_id", nullable = false)
    private College college;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Folder> folders = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notifications = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SolvedWorkbook> solvedWorkbooks = new ArrayList<>();

    @Builder
    public User(String name, String nickname, String email, String password, College college, Department department) {
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.profileImage = 0;
        this.level = 1;
        this.exp = 0;
        this.status = UserStatus.ACTIVE;
        this.college = college;
        this.department = department;
    }

    public void updateProfileImage(Integer profileImage){
        this.profileImage = profileImage;
    }

    public void gainExp(Integer exp){
        this.exp += exp;
        while(UserLevelPolicy.canLevelUp(this.level, this.exp)){
            this.exp -= UserLevelPolicy.getRequiredExp(this.level);
            this.level++;
        }
        if(!UserLevelPolicy.canLevelUp(this.level, this.exp) && this.exp > UserLevelPolicy.getRequiredExp(this.level)){
            this.exp = UserLevelPolicy.getRequiredExp(this.level);
        }
    }
}
