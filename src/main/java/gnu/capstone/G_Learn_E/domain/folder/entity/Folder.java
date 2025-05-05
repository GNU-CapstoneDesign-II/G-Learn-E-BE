package gnu.capstone.G_Learn_E.domain.folder.entity;

import gnu.capstone.G_Learn_E.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Folder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String name;

    private LocalDateTime createdAt;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Folder parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Folder> children = new ArrayList<>();


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


    @OneToMany(mappedBy = "folder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FolderWorkbookMap> folderWorkbookMaps = new ArrayList<>();

    @PrePersist
    public void prePersist(){
        this.createdAt = LocalDateTime.now();
    }


    @Builder
    public Folder(String name, Folder parent, User user) {
        this.name = name;
        this.parent = parent;
        this.user = user;
        this.createdAt = LocalDateTime.now();
    }
}
