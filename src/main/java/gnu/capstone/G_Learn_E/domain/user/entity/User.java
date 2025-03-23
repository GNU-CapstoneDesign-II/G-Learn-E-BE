package gnu.capstone.G_Learn_E.domain.user.entity;

import gnu.capstone.G_Learn_E.global.common.enums.UserStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nickname;

    @Column(unique = true, nullable = false)
    private String email;

    @Column
    private Integer profileImage;

    @Column
    private Short level;

    @Column
    private Integer exp;

    @Column
    private UserStatus status;

    @Builder
    public User(String nickname, String email){
        this.nickname = nickname;
        this.email = email;
        this.profileImage = 0;
        this.level = 1;
        this.exp = 0;
        this.status = UserStatus.ACTIVE;
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
