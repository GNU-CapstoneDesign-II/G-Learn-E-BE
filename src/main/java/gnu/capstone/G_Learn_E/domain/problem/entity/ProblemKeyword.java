package gnu.capstone.G_Learn_E.domain.problem.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class ProblemKeyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String keyword;

    private int priority;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id")
    private Problem problem;


    @Builder
    public ProblemKeyword(String keyword, int priority, Problem problem) {
        this.keyword = keyword;
        this.priority = priority;
        this.problem = problem;
    }
}
