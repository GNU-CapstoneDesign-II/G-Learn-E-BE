package gnu.capstone.G_Learn_E.domain.solve_log.entity;

import gnu.capstone.G_Learn_E.domain.problem.entity.Problem;
import gnu.capstone.G_Learn_E.global.common.serialization.converter.AnswerListConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class SolveLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Convert(converter = AnswerListConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<String> submitAnswer; // 제출한 답안

    boolean isCorrect; // 정답 여부

    private LocalDateTime createdAt; // 생성일시


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solved_workbook_id")
    private SolvedWorkbook solvedWorkbook; // 푼 문제집

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id")
    private Problem problem; // 푼 문제
}
