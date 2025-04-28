package gnu.capstone.G_Learn_E.domain.problem.entity;


import gnu.capstone.G_Learn_E.domain.workbook.entity.Workbook;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Table
@Getter
@NoArgsConstructor(access = PROTECTED)
public class ProblemWorkbookMap {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                    // << 단일 PK 로 변경

    @ManyToOne(fetch = LAZY)             // 외래키만 남김
    @JoinColumn(name = "workbook_id")
    private Workbook workbook;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "problem_id")
    private Problem problem;

    @Column(nullable=false)
    private Integer problemNumber;

    // 생성자
    @Builder
    public ProblemWorkbookMap(Workbook workbook, Problem problem, Integer problemNumber) {
        this.workbook = workbook;
        this.problem  = problem;
        this.problemNumber = problemNumber;
    }
}
