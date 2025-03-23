package gnu.capstone.G_Learn_E.domain.workbook.entity;

import gnu.capstone.G_Learn_E.domain.curriculm.entity.SubjectWorkbookMap;
import gnu.capstone.G_Learn_E.domain.problem.entity.Problem;
import gnu.capstone.G_Learn_E.domain.solve_log.entity.SolvedWorkbook;
import gnu.capstone.G_Learn_E.domain.workbook.enums.ExamType;
import gnu.capstone.G_Learn_E.domain.workbook.enums.Semester;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Workbook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String professor;

    @Enumerated(EnumType.STRING)
    private ExamType examType;

    @Column
    private Integer coverImage;

    @Column
    private Integer courseYear;

    @Enumerated(EnumType.STRING)
    private Semester semester;

    @Column
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "workbook", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubjectWorkbookMap> subjectWorkbookMaps = new ArrayList<>();

    @OneToMany(mappedBy = "workbook", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Problem> problems = new ArrayList<>();

    @OneToMany(mappedBy = "workbook", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SolvedWorkbook> solvedWorkbooks = new ArrayList<>();

    @PrePersist
    public void prePersist(){
        this.createdAt = LocalDateTime.now();
    }

    @Builder
    public Workbook(String name, String professor, ExamType examType, Integer coverImage, Integer courseYear, Semester semester){
        this.name = name;
        this.professor = professor;
        this.examType = examType;
        this.coverImage = coverImage;
        this.courseYear = courseYear;
        this.semester = semester;
        this.createdAt = LocalDateTime.now();
    }
}
