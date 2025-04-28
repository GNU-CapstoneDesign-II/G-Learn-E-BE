package gnu.capstone.G_Learn_E.global.admin;


import gnu.capstone.G_Learn_E.global.admin.dto.CleanupResult;
import gnu.capstone.G_Learn_E.global.scheduler.OrphanCleanupScheduler;
import gnu.capstone.G_Learn_E.global.template.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final OrphanCleanupScheduler cleanupScheduler;

    /** 관리자 전용 즉시 정리 엔드포인트 */
    @DeleteMapping("/orphans")
    public ApiResponse<CleanupResult> triggerCleanup() {
        CleanupResult result = cleanupScheduler.cleanUpNow();
        return new ApiResponse<>(
                HttpStatus.OK,
                "고아 엔티티 정리 완료",
                result
        );
    }
}
