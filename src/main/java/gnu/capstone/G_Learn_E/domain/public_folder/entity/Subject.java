package gnu.capstone.G_Learn_E.domain.public_folder.entity;

import gnu.capstone.G_Learn_E.domain.public_folder.enums.SubjectGrade;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private SubjectGrade grade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @OneToMany(mappedBy = "subject", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubjectWorkbookMap> subjectWorkbookMaps = new ArrayList<>();

    @Builder
    public Subject(String name, SubjectGrade grade, Department department) {
        this.name = name;
        this.grade = grade;
        this.department = department;
    }
}
