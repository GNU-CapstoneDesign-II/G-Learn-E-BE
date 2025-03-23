package gnu.capstone.G_Learn_E.domain.workbook.entity;

import gnu.capstone.G_Learn_E.domain.workbook.enums.ExamType;
import gnu.capstone.G_Learn_E.domain.workbook.enums.Semester;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
