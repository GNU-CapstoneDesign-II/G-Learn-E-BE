package gnu.capstone.G_Learn_E.domain.problem.entity;

import gnu.capstone.G_Learn_E.domain.solve_log.entity.SolveLog;
import gnu.capstone.G_Learn_E.global.common.serialization.converter.AnswerListConverter;
import gnu.capstone.G_Learn_E.global.common.serialization.converter.OptionListConverter;
import gnu.capstone.G_Learn_E.domain.problem.enums.ProblemType;
import gnu.capstone.G_Learn_E.global.common.serialization.Option;
import gnu.capstone.G_Learn_E.domain.workbook.entity.Workbook;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Problem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 문제 번호

    private Integer problemNumber; // 문제 번호 (문제집 내에서의 순서)

    private String title;        // 문제 제목

    @Convert(converter = OptionListConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<Option> options; // 객관식 보기, 나머지는 null

    @Convert(converter = AnswerListConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<String> answers; // 정답 리스트 (빈칸 복수, 나머지 단일)

    @Column(columnDefinition = "TEXT")
    private String explanation;  // 해설

    @Enumerated(EnumType.STRING)
    private ProblemType type;    // 문제 유형 (객관식, OX, 주관식 등)


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workbook_id")
    private Workbook workbook;    // 문제가 속한 문제집

    @OneToMany(mappedBy = "problem", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SolveLog> solveLogs; // 문제를 푼 사용자들의 풀이 기록

    @Builder
    public Problem(Integer problemNumber, String title, List<Option> options, List<String> answers,
                   String explanation, ProblemType type) {
        this.problemNumber = problemNumber;
        this.title = title;
        this.options = options;
        this.answers = answers;
        this.explanation = explanation;
        this.type = type;
    }
}
