package gnu.capstone.G_Learn_E.domain.workbook.converter;

import gnu.capstone.G_Learn_E.domain.problem.entity.Problem;
import gnu.capstone.G_Learn_E.domain.workbook.dto.response.ProblemGenerateResponse;
import gnu.capstone.G_Learn_E.domain.workbook.entity.Workbook;

import java.util.ArrayList;
import java.util.List;

import static gnu.capstone.G_Learn_E.domain.problem.converter.ProblemConverter.*;

public class WorkbookConverter {

    /**
     * ProblemGenerateResponse DTO를 기반으로 Workbook 객체와 해당 Workbook에 포함될 Problem 엔티티들을 생성합니다.
     *
     * @param response Problem 생성 DTO
     * @param workbook 변환 대상 Workbook 엔티티 (기본 정보는 이미 셋팅되어 있다고 가정)
     * @return 문제들이 추가된 Workbook 객체
     */
    public static Workbook convertToWorkbookAndProblems(ProblemGenerateResponse response, Workbook workbook) {
        List<Problem> problems = new ArrayList<>();

        Integer problemNumber = 1; // 문제 번호 초기화

        // 1. 객관식 문제 변환
        for (ProblemGenerateResponse.MultipleChoice mc : response.result().multipleChoice()) {
            Problem problem = convertToMultipleProblem(mc, problemNumber++, workbook);
            problems.add(problem);
        }

        // 2. OX 문제 변환
        for (ProblemGenerateResponse.Ox ox : response.result().ox()) {
            Problem problem = convertToOxProblem(ox, problemNumber++, workbook);
            problems.add(problem);
        }

        // 3. 빈칸 채우기 문제 변환
        for (ProblemGenerateResponse.FillInTheBlank fib : response.result().fillInTheBlank()) {
            Problem problem = convertToBlankProblem(fib, problemNumber++, workbook);
            problems.add(problem);
        }

        // 4. 서술형 문제 변환
        for (ProblemGenerateResponse.Descriptive desc : response.result().descriptive()) {
            Problem problem = convertToDescriptiveProblem(desc, problemNumber++, workbook);
            problems.add(problem);
        }

        // Workbook의 문제 목록에 추가 (양방향 연관관계 설정)
        workbook.getProblems().addAll(problems);
        return workbook;
    }
}
