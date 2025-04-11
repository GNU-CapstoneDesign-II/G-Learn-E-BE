package gnu.capstone.G_Learn_E.domain.problem.converter;

import gnu.capstone.G_Learn_E.domain.problem.entity.Problem;
import gnu.capstone.G_Learn_E.domain.problem.enums.ProblemType;
import gnu.capstone.G_Learn_E.domain.workbook.dto.response.ProblemGenerateResponse;
import gnu.capstone.G_Learn_E.global.common.serialization.Option;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ProblemConverter {

    /**
     * 객관식 문제를 Problem 엔티티로 변환합니다.
     * @param mc 객관식 문제 DTO
     * @return 변환된 Problem 엔티티
     */
    public static Problem convertToMultipleProblem(
            ProblemGenerateResponse.MultipleChoice mc, Integer problemNumber) {
        return Problem.builder()
                .problemNumber(problemNumber)
                .title(mc.question())
                .options(convertOptions(mc.options()))
                .answers(Collections.singletonList(mc.answer()))
                .explanation(mc.explanation())
                .type(ProblemType.MULTIPLE)
                .build();
    }


    /**
     * OX 문제를 Problem 엔티티로 변환합니다.
     * @param ox OX 문제 DTO
     * @return 변환된 Problem 엔티티
     */
    public static Problem convertToOxProblem(ProblemGenerateResponse.Ox ox, Integer problemNumber) {
        return Problem.builder()
                .problemNumber(problemNumber)
                .title(ox.question())
                .options(null)
                .answers(Collections.singletonList(ox.answer()))
                .explanation(ox.explanation())
                .type(ProblemType.OX)
                .build();
    }

    /**
     * 빈칸 채우기 문제를 Problem 엔티티로 변환합니다.
     * @param fib 빈칸 채우기 문제 DTO
     * @return 변환된 Problem 엔티티
     */
    public static Problem convertToBlankProblem(ProblemGenerateResponse.FillInTheBlank fib, Integer problemNumber) {
        return Problem.builder()
                .problemNumber(problemNumber)
                .title(fib.question())
                .options(null)
                .answers(fib.answer())
                .explanation(fib.explanation())
                .type(ProblemType.BLANK)
                .build();
    }

    /**
     * 서술형 문제를 Problem 엔티티로 변환합니다.
     * @param desc 서술형 문제 DTO
     * @return 변환된 Problem 엔티티
     */
    public static Problem convertToDescriptiveProblem(ProblemGenerateResponse.Descriptive desc, Integer problemNumber) {
        return Problem.builder()
                .problemNumber(problemNumber)
                .title(desc.question())
                .options(null)
                .answers(Collections.singletonList(desc.answer()))
                // 서술형 문제는 해설이 없는 경우도 있음
                .explanation(null)
                .type(ProblemType.DESCRIPTIVE)
                .build();
    }


    /**
     * 문자열 목록을 Option 객체의 목록으로 변환합니다.
     * @param optionsList 객관식 보기 문자열 목록
     * @return Option 객체 목록
     */
    private static List<Option> convertOptions(List<String> optionsList) {
        if (optionsList == null) {
            return null;
        }

        return IntStream.range(0, optionsList.size())
                .mapToObj(i -> Option.builder()
                        .number((short) (i + 1)) // 1부터 시작하는 번호
                        .content(optionsList.get(i))
                        .build())
                .collect(Collectors.toList());
    }
}
