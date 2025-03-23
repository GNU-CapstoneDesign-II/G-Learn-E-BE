package gnu.capstone.G_Learn_E.domain.problem.entity;

import gnu.capstone.G_Learn_E.domain.problem.serialization.converter.AnswerListConverter;
import gnu.capstone.G_Learn_E.domain.problem.serialization.converter.OptionListConverter;
import gnu.capstone.G_Learn_E.domain.problem.enums.ProblemType;
import gnu.capstone.G_Learn_E.domain.problem.serialization.Option;
import jakarta.persistence.*;
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

    private String title;        // 문제 제목

    @Column(columnDefinition = "TEXT")
    private String content;      // 문제 본문 내용

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
}
