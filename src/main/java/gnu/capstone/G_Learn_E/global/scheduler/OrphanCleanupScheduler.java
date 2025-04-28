package gnu.capstone.G_Learn_E.global.scheduler;

import gnu.capstone.G_Learn_E.domain.problem.entity.Problem;
import gnu.capstone.G_Learn_E.domain.problem.repository.ProblemRepository;
import gnu.capstone.G_Learn_E.domain.workbook.entity.Workbook;
import gnu.capstone.G_Learn_E.domain.workbook.repository.WorkbookRepository;
import gnu.capstone.G_Learn_E.global.admin.dto.CleanupResult;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrphanCleanupScheduler {

    private final WorkbookRepository workbookRepo;
    private final ProblemRepository  problemRepo;

    /** 스케줄러(새벽 3시)용 */
    @Scheduled(cron = "0 0 3 * * *", zone = "Asia/Seoul")
    public void scheduledCleanUp() {
        CleanupResult result = cleanUpInternal();
        log.info("스케줄러 실행: 워크북 {}건, 문제 {}건 삭제",
                result.deletedWorkbooks(), result.deletedProblems());
    }

    /** API에서 바로 호출할 메서드 */
    @Transactional
    public CleanupResult cleanUpNow() {
        return cleanUpInternal();
    }

    /** 실제 정리 로직 */
    private CleanupResult cleanUpInternal() {

        List<Workbook> orphanWb = workbookRepo.findOrphanWorkbooks();
        int wbCnt = orphanWb.size();
        if (wbCnt > 0) workbookRepo.deleteAll(orphanWb);

        List<Problem> orphanPb = problemRepo.findOrphanProblems();
        int pbCnt = orphanPb.size();
        if (pbCnt > 0) problemRepo.deleteAll(orphanPb);

        return new CleanupResult(wbCnt, pbCnt);
    }
}

