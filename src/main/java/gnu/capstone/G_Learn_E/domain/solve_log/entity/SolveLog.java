package gnu.capstone.G_Learn_E.domain.solve_log.entity;

import gnu.capstone.G_Learn_E.domain.problem.entity.Problem;
import gnu.capstone.G_Learn_E.global.common.serialization.converter.AnswerListConverter;
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
public class SolveLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Convert(converter = AnswerListConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<String> submitAnswer; // 제출한 답안

    @Column(nullable = true)
    Boolean isCorrect; // 정답 여부

    private LocalDateTime createdAt; // 생성일시


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "user_id", referencedColumnName = "user_id"),
            @JoinColumn(name = "workbook_id", referencedColumnName = "workbook_id")
    })
    private SolvedWorkbook solvedWorkbook; // 푼 문제집

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id")
    private Problem problem; // 푼 문제


    @Builder
    public SolveLog(SolvedWorkbook solvedWorkbook, Problem problem){
        this.isCorrect = null;
        this.submitAnswer = new ArrayList<>();
        this.solvedWorkbook = solvedWorkbook;
        this.problem = problem;
        this.createdAt = LocalDateTime.now();
    }

    public Long getProblemId() {
        return this.problem.getId();
    }
}
