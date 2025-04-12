package gnu.capstone.G_Learn_E.domain.problem.service;

import gnu.capstone.G_Learn_E.domain.problem.entity.Problem;
import gnu.capstone.G_Learn_E.domain.problem.repository.ProblemRepository;
import gnu.capstone.G_Learn_E.domain.workbook.entity.Workbook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProblemService {

    private final ProblemRepository problemRepository;


    public List<Problem> findAllByWorkbookId(Workbook workbook) {
        return problemRepository.findAllByWorkbookId(workbook.getId());
    }
}
