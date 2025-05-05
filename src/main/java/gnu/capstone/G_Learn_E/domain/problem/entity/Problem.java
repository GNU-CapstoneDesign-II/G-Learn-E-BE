package gnu.capstone.G_Learn_E.domain.problem.entity;

import gnu.capstone.G_Learn_E.domain.solve_log.entity.SolveLog;
import gnu.capstone.G_Learn_E.domain.workbook.entity.Workbook;
import gnu.capstone.G_Learn_E.global.common.serialization.converter.AnswerListConverter;
import gnu.capstone.G_Learn_E.global.common.serialization.converter.OptionListConverter;
import gnu.capstone.G_Learn_E.domain.problem.enums.ProblemType;
import gnu.capstone.G_Learn_E.global.common.serialization.Option;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Problem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Convert(converter = OptionListConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<Option> options;

    @Convert(converter = AnswerListConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<String> answers;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    @Enumerated(EnumType.STRING)
    private ProblemType type;

    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProblemWorkbookMap> problemWorkbookMaps = new ArrayList<>();

    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SolveLog> solveLogs = new ArrayList<>();

    @Builder
    public Problem(String title,
                   List<Option> options,
                   List<String> answers,
                   String explanation,
                   ProblemType type) {
        this.title = title;
        this.options = options;
        this.answers = answers;
        this.explanation = explanation;
        this.type = type;
    }

    /**
     * 편의 메서드: 이 문제를 워크북에 매핑합니다.
     */
    public void addToWorkbook(Workbook workbook, Integer problemNumber) {
        ProblemWorkbookMap map = new ProblemWorkbookMap(workbook, this, problemNumber);
        this.problemWorkbookMaps.add(map);
        workbook.getProblemWorkbookMaps().add(map);
    }
}
