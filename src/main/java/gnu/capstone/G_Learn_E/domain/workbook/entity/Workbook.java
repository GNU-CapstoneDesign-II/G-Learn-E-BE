package gnu.capstone.G_Learn_E.domain.workbook.entity;

import gnu.capstone.G_Learn_E.domain.folder.entity.FolderWorkbookMap;
import gnu.capstone.G_Learn_E.domain.problem.entity.ProblemWorkbookMap;
import gnu.capstone.G_Learn_E.domain.problem.entity.Problem;
import gnu.capstone.G_Learn_E.domain.public_folder.entity.SubjectWorkbookMap;
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

    private String name;
    private String professor;

    @Enumerated(EnumType.STRING)
    private ExamType examType;

    private Integer coverImage;
    private Integer courseYear;

    @Enumerated(EnumType.STRING)
    private Semester semester;

    private LocalDateTime createdAt;
    private boolean isUploaded;

    @OneToMany(mappedBy = "workbook", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubjectWorkbookMap> subjectWorkbookMaps = new ArrayList<>();

    @OneToMany(mappedBy = "workbook", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FolderWorkbookMap> folderWorkbookMaps = new ArrayList<>();

    @OneToMany(mappedBy = "workbook", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("problemNumber ASC")
    private List<ProblemWorkbookMap> problemWorkbookMaps = new ArrayList<>();

    @OneToMany(mappedBy = "workbook", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SolvedWorkbook> solvedWorkbooks = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Builder
    public Workbook(String name,
                    String professor,
                    ExamType examType,
                    Integer coverImage,
                    Integer courseYear,
                    Semester semester) {
        this.name = name;
        this.professor = professor;
        this.examType = examType;
        this.coverImage = coverImage;
        this.courseYear = courseYear;
        this.semester = semester;
        this.isUploaded = false;
    }

    /**
     * 문제를 이 워크북에 매핑합니다.
     */
    public void addProblem(Problem problem, Integer problemNumber) {
        ProblemWorkbookMap map = ProblemWorkbookMap.builder()
                .workbook(this)
                .problem(problem)
                .problemNumber(problemNumber)
                .build();
        this.problemWorkbookMaps.add(map);
        problem.getProblemWorkbookMaps().add(map);
    }
}