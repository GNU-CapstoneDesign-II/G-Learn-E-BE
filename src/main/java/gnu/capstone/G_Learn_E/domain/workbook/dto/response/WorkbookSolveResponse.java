package gnu.capstone.G_Learn_E.domain.workbook.dto.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import gnu.capstone.G_Learn_E.domain.problem.dto.response.ProblemResponse;

import java.util.List;

public record WorkbookSolveResponse(
        @JsonProperty("workbook") WorkbookInfo workbook,
        @JsonProperty("problems")List<ProblemInfo> problems
) {

    public static WorkbookSolveResponse from(
            WorkbookInfo workbookInfo,
            List<ProblemInfo> problems
    ) {
        return new WorkbookSolveResponse(workbookInfo, problems);
    }

    public record WorkbookInfo(
            Long id,
            String name,
            boolean isSolved,
            Integer correctCount,
            Integer wrongCount
    ){
        public static WorkbookInfo of(
                Long id,
                String name,
                boolean isSolved,
                Integer correctCount,
                Integer wrongCount
        ) {
            return new WorkbookInfo(id, name, isSolved, correctCount, wrongCount);
        }

    }

    public record ProblemInfo(
        ProblemResponse problem,
        UserAttempt userAttepmt
    ){
        public static ProblemInfo from(
                ProblemResponse problem,
                UserAttempt userAttepmt
        ) {
            return new ProblemInfo(problem, userAttepmt);
        }

        public record UserAttempt(
                List<String> submitAnswer,
                Boolean isCorrect
        ){
            public static UserAttempt of(
                    List<String> submitAnswer,
                    Boolean isCorrect
            ) {
                return new UserAttempt(submitAnswer, isCorrect);
            }
        }
    }

}
