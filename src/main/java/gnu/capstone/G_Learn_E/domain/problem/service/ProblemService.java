package gnu.capstone.G_Learn_E.domain.problem.service;

import gnu.capstone.G_Learn_E.domain.problem.entity.Problem;
import gnu.capstone.G_Learn_E.domain.problem.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class ProblemService {

    private final ProblemRepository problemRepository;


    /**
     * 문제집 ID 리스트에 속하는 문제 조회
     * @param workbookIds 문제집 ID 리스트
     * @return 문제 리스트
     */
    public List<Problem> findAllByWorkbookIds(List<Long> workbookIds) {
        return problemRepository.findAllByProblemWorkbookMaps_Workbook_IdIn(workbookIds);
    }
}
