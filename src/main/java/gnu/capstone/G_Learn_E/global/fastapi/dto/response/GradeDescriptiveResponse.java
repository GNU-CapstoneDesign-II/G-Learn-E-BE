package gnu.capstone.G_Learn_E.global.fastapi.dto.response;

import java.util.List;

public record GradeDescriptiveResponse(
        List<GradedProblem> result
) {

    public record GradedProblem(
            Long id,
            boolean correct
    ) {
    }
}
